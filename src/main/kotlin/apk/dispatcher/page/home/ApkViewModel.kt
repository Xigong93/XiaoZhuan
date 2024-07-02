package apk.dispatcher.page.home

import androidx.compose.runtime.*
import apk.dispatcher.AppPath
import apk.dispatcher.channel.ChannelRegistry
import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.SubmitState
import apk.dispatcher.channel.TaskLauncher
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
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

    init {
        selectedChannels.addAll(channels.map { it.channelName })
        loadMarketState()
    }


    fun getApkDirState(): State<File?> = apkDirState

    fun getApkInfoState(): State<ApkInfo?> = apkInfoState

    fun selectedApkDir(dir: File): Boolean {
        return try {
            val apkFile = if (dir.isDirectory) AppPath.listApk(dir).first() else dir
            val launchers = selectedLaunchers()
            launchers.forEach { it.selectFile(dir) }
            apkInfoState.value = runBlocking { getApkInfo(apkFile) }
            apkDirState.value = dir
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
        mainScope.launch {
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
        mainScope.launch {
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
        lastUpdateMarketStateTime = System.currentTimeMillis()
        mainScope.launch {
            loadingMarkState = true
            supervisorScope {
                taskLaunchers.forEach {
                    async { it.loadMarketState(apkConfig.applicationId) }
                }
            }
            loadingMarkState = false
        }
    }

    /**
     * 取消分发
     */
    fun cancelDispatch() {
        mainScope.cancel("用户取消")
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
            selectedChannels.addAll(channels.map { it.channelName })
        } else {
            selectedChannels.clear()
        }
    }

    /**
     * 选择某个渠道
     */
    fun selectChannel(name: String, selected: Boolean) {
        if (selected) {
            selectedChannels.remove(name)
            selectedChannels.add(name)
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
}
