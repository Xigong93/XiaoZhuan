package apk.dispatcher.page.home

import androidx.compose.runtime.*
import androidx.lifecycle.AtomicReference
import apk.dispatcher.AppPath
import apk.dispatcher.channel.ChannelRegistry
import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.TaskLauncher
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.log.AppLogger
import apk.dispatcher.page.upload.UploadParam
import apk.dispatcher.util.ApkInfo
import apk.dispatcher.util.FileSelector
import apk.dispatcher.util.FileUtil
import apk.dispatcher.util.getApkInfo
import apk.dispatcher.widget.Toast
import kotlinx.coroutines.*
import java.io.File

class ApkPageState(val apkConfig: ApkConfig) {

    private val scope = MainScope()

    private val apkDirState = mutableStateOf<File?>(null)

    private val apkInfoState = mutableStateOf<ApkInfo?>(null)

    val updateDesc = mutableStateOf(apkConfig.extension.updateDesc ?: "")


    val channels: List<ChannelTask> = ChannelRegistry.channels

    /**
     * 选中的Channel
     */
    val selectedChannels = mutableStateListOf<String>()

    /**
     * 应用市场信息加载状态
     */
    var loadingMarkState by mutableStateOf(false)

    val taskLaunchers: List<TaskLauncher> = channels.map(::TaskLauncher)


    var lastUpdateMarketStateTime = 0L


    init {
        AppLogger.info(LOG_TAG, "init")
        loadMarketState()
    }


    fun getApkDirState(): State<File?> = apkDirState

    fun getApkInfoState(): State<ApkInfo?> = apkInfoState

    private suspend fun parseApkFile(dir: File): Boolean {
        return try {
            val apkFile = if (dir.isDirectory) AppPath.listApk(dir).first() else dir
            taskLaunchers.forEach {
                it.setChannelParam(apkConfig.channels)
                it.selectFile(dir)
            }
            apkInfoState.value = getApkInfo(apkFile)
            apkDirState.value = dir
            updateSelectChannel()
            true
        } catch (e: Exception) {
            AppLogger.error(LOG_TAG, "解析选择Apk失败", e)
            e.printStackTrace()
            false
        }
    }


    /**
     * 获取应用市场状态
     */
    fun loadMarketState() {
        AppLogger.info(LOG_TAG, "更新应用市场审核状态")
        lastUpdateMarketStateTime = System.currentTimeMillis()
        val apkConfig = requireNotNull(apkConfig)
        scope.launch {
            loadingMarkState = true
            supervisorScope {
                taskLaunchers.forEach {
                    it.setChannelParam(apkConfig.channels)
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


    private fun updateApkConfig() {
        val updateDesc = updateDesc.value.trim()
        val apkDir = apkDirState.value ?: return
        val newExtension = apkConfig.extension.copy(
            apkDir = apkDir.absolutePath,
            updateDesc = updateDesc
        )
        scope.launch {
            val configDao = ApkConfigDao()
            try {
                configDao.saveConfig(apkConfig.copy(extension = newExtension))
            } catch (e: Exception) {
                AppLogger.error(LOG_TAG, "更新Apk配置失败", e)
            }
        }
    }

    /**
     * 获取上一次选择的Apk文件或目录
     */
    private fun getLastApkDir(): File? {
        val apkFile = apkConfig.extension.apkDir ?: return null
        return File(apkFile).takeIf { it.exists() }
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

    fun startDispatch(): UploadParam? {
        val file = getApkDirState().value
        if (file == null) {
            Toast.show("请选择Apk文件")
            return null
        }
        val updateDesc = updateDesc.value.trim()
        if (updateDesc.isEmpty()) {
            Toast.show("请输入更新描述")
            return null
        }
        if (updateDesc.length > 300) {
            Toast.show("更新描述不可超过300字")
            return null

        }
        val channels = selectedChannels
        if (channels.isEmpty()) {
            Toast.show("请选择渠道")
            return null
        }
        updateApkConfig()
        return UploadParam(
            appId = apkConfig.applicationId,
            updateDesc = updateDesc,
            channels = channels,
            apkFile = file.absolutePath
        )
    }

    fun getFileSize(): String {
        val apkInfo = getApkInfoState().value ?: return ""
        val file = File(apkInfo.path)
        return FileUtil.getFileSize(file)
    }


    fun selectedApkDir() {
        scope.launch {
            val dir = FileSelector.selectedDir(getLastApkDir())
            if (dir != null && !parseApkFile(dir)) {
                Toast.show("无效目录,未包含有效的Apk文件")
            }
        }
    }


    fun selectApkFile() {
        scope.launch {
            val file = FileSelector.selectedFile(getLastApkDir(), "*.apk", listOf("apk"))
            if (file != null && !parseApkFile(file)) {
                Toast.show("无效的Apk文件")
            }
        }
    }


    fun clear() {
        if (scope.isActive) scope.cancel()
        AppLogger.info(LOG_TAG, "clear ${apkConfig.applicationId}")
    }


    companion object {
        private const val LOG_TAG = "应用界面"
    }


}
