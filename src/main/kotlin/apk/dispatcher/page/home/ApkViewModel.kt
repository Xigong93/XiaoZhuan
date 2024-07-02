package apk.dispatcher.page.home

import androidx.compose.runtime.*
import androidx.lifecycle.AtomicReference
import apk.dispatcher.AppPath
import apk.dispatcher.channel.ChannelRegistry
import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.SubmitState
import apk.dispatcher.channel.TaskLauncher
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import apk.dispatcher.util.FileUtil
import apk.dispatcher.util.getApkInfo
import apk.dispatcher.widget.Toast
import kotlinx.coroutines.*
import java.io.File

class ApkViewModel(
    val apkConfig: ApkConfig
) {

    private val mainScope = MainScope()

    val updateDesc = mutableStateOf(apkConfig.extension.updateDesc ?: "")

    private val apkDirState = mutableStateOf<File?>(null)

    private val apkInfoState = mutableStateOf<ApkInfo?>(null)

    val channels: List<ChannelTask> =
        ChannelRegistry.channels.filter { apkConfig.getChannel(it.channelName)?.enable == true }

    /**
     * 选中的Channel
     */
    val selectedChannels = mutableStateListOf<String>()

    /**
     * 应用市场信息加载状态
     */
    var loadingMarkState by mutableStateOf(false)

    val taskLaunchers: List<TaskLauncher> = channels.map { TaskLauncher(apkConfig, it) }


    var lastUpdateMarketStateTime = 0L

    var submitJob: Job? = null

    init {
        loadMarketState()
    }


    fun getApkDirState(): State<File?> = apkDirState

    fun getApkInfoState(): State<ApkInfo?> = apkInfoState

    fun selectedApkDir(dir: File): Boolean {
        return try {
            val apkFile = if (dir.isDirectory) AppPath.listApk(dir).first() else dir
            taskLaunchers.forEach { it.selectFile(dir) }
            apkInfoState.value = runBlocking { getApkInfo(apkFile) }
            apkDirState.value = dir
            updateSelectChannel()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 开始分发
     */
    fun startDispatch() {
        AppLogger.info(LOG_TAG, "开始分发")
        submitJob?.takeIf { it.isActive }?.cancel()
        submitJob = mainScope.launch {
            val launchers = selectedLaunchers()
            val updateDesc = requireNotNull(updateDesc.value).trim()
            launchers.forEach {
                it.prepare()
            }
            updateApkConfig()
            launchers.forEach {
                it.startSubmit(updateDesc)
            }
        }
    }

    /**
     * 重试
     */
    fun retryDispatch() {
        AppLogger.info(LOG_TAG, "重试")
        submitJob?.takeIf { it.isActive }?.cancel()
        submitJob = mainScope.launch {
            val launchers = selectedLaunchers().filter { it.getSubmitState().value is SubmitState.Error }
            val updateDesc = requireNotNull(updateDesc.value).trim()
            launchers.forEach {
                it.prepare()
            }
            updateApkConfig()
            launchers.forEach {
                it.startSubmit(updateDesc)
            }
        }
    }

    /**
     * 获取应用市场状态
     */
    fun loadMarketState() {
        AppLogger.info(LOG_TAG, "更新应用市场审核状态")
        lastUpdateMarketStateTime = System.currentTimeMillis()
        mainScope.launch {
            loadingMarkState = true
            supervisorScope {
                taskLaunchers.forEach {
                    async { it.loadMarketState(apkConfig.applicationId) }
                }
            }
            loadingMarkState = false
            updateSelectChannel()
        }
    }

    private fun updateSelectChannel() {
        if (selectedChannels.isEmpty()) {
            selectAll(true)
        } else {
            // 删除失效的
            selectedChannels
                .filterNot { checkChannelEnableSubmit(it) }
                .forEach {
                    selectChannel(it, false)
                }
        }
    }

    /**
     * 取消分发
     */
    fun cancelDispatch() {
        AppLogger.info(LOG_TAG, "取消分发")
        submitJob?.takeIf { it.isActive }?.cancel("用户取消")
    }


    private fun updateApkConfig() {
        val updateDesc = requireNotNull(updateDesc.value).trim()
        val apkDir = requireNotNull(apkDirState.value)
        val newExtension = apkConfig.extension.copy(
            apkDir = apkDir.absolutePath,
            updateDesc = updateDesc
        )
        val configDao = ApkConfigDao()
        configDao.saveConfig(apkConfig.copy(extension = newExtension))
    }

    /**
     * 获取上一次选择的Apk文件或目录
     */
    fun getLastApkDir(): File? {
        return apkConfig.extension.apkDir?.let { File(it) }?.takeIf { it.exists() }
    }


    /**
     * 全部渠道已选择
     */
    fun allChannelSelected(): Boolean {
        return selectedChannels.containsAll(channels.map { it.channelName })
    }

    /**
     * 选择全部渠道
     */
    fun selectAll(selectedAll: Boolean) {
        if (selectedAll) {
            selectedChannels.clear()
            selectedChannels.addAll(getEnableSubmitChannel())
        } else {
            selectedChannels.clear()
        }
    }

    /**
     * 获取可以上传的渠道
     */
    private fun getEnableSubmitChannel(): List<String> {
        return taskLaunchers.filter { checkChannelEnableSubmit(it.name) }.map { it.name }
    }

    /**
     * 检查当前渠道是否支持提交
     */
    private fun checkChannelEnableSubmit(channelName: String, message: AtomicReference<String>? = null): Boolean {
        val task = taskLaunchers.firstOrNull { it.name == channelName } ?: return false
        val marketState = task.getMarketState().value?.getOrNull()
        val apkInfo = getApkInfoState().value
        if (marketState != null && !marketState.enableSubmit) {
            message?.set("应用市场审核中，或状态异常，无法上传新版本")
            return false
        }
        if (apkInfo != null && marketState != null && apkInfo.versionCode <= marketState.lastVersionCode) {
            message?.set("要提交的Apk版本号需大于线上最新版本号")
            return false
        }
        return true
    }

    /**
     * 选择某个渠道
     */
    fun selectChannel(name: String, selected: Boolean) {
        if (selected) {
            val message = AtomicReference<String>()
            if (!checkChannelEnableSubmit(name, message)) {
                message.get()?.let { Toast.show(it) }
            } else {
                selectedChannels.remove(name)
                selectedChannels.add(name)
            }
        } else {
            selectedChannels.remove(name)
        }
    }

    fun selectedLaunchers() = taskLaunchers.filter { selectedChannels.contains(it.name) }

    fun checkForm(): Boolean {
        if (getApkDirState().value == null) {
            Toast.show("请选择Apk文件")
            return false
        }
        if (updateDesc.value.isEmpty()) {
            Toast.show("请输入更新描述")
            return false
        }
        if (updateDesc.value.length > 300) {
            Toast.show("更新描述不可超过300字")
            return false
        }
        if (selectedChannels.isEmpty()) {
            Toast.show("请选择渠道")
            return false
        }
        return true
    }

    fun getFileSize(): String {
        val apkInfo = getApkInfoState().value ?: return ""
        val file = File(apkInfo.path)
        return FileUtil.getFileSize(file)
    }

    companion object {
        private const val LOG_TAG = "应用市场提交"
    }
}
