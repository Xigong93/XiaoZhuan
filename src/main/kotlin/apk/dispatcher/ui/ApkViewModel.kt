package apk.dispatcher.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import apk.dispatcher.ApkChannelRegistry
import apk.dispatcher.ApkChannelTask
import apk.dispatcher.ApkConfig
import apk.dispatcher.ApkConfigDao
import apk.dispatcher.util.ApkInfo
import apk.dispatcher.util.PathUtil
import apk.dispatcher.util.getApkInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

class ApkViewModel(
    private val apkConfig: ApkConfig
) {

    private val mainScope = CoroutineScope(EmptyCoroutineContext)

    val updateDesc = mutableStateOf<String>(apkConfig.extension.updateDesc ?: "")

    private val apkDirState = mutableStateOf<File?>(null)

    private val apkInfoState = mutableStateOf<ApkInfo?>(null)

    val channels: List<ApkChannelTask> =
        ApkChannelRegistry.channels.filter { apkConfig.getChannel(it.channelName)?.enable == true }

    /**
     * 选中的Channel
     */
    val selectedChannels = mutableStateListOf<String>()


    val taskLaunchers: List<TaskLauncher> = channels.map { TaskLauncher(apkConfig, it) }

    init {
        selectedChannels.addAll(channels.map { it.channelName })
    }


    fun getApkDirState(): State<File?> = apkDirState

    fun getApkInfoState(): State<ApkInfo?> = apkInfoState

    fun selectedApkDir(dir: File): Boolean {
        return try {
            val apkFile = if (apkConfig.extension.enableChannel) {
                PathUtil.listApkFile(dir).first()
            } else {
                dir
            }
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




    fun startDispatch() {
        mainScope.launch {
            val launchers = selectedLaunchers()
            val updateDesc = requireNotNull(updateDesc.value).trim()
            launchers.forEach {
                it.prepare()
            }
            updateApkConfig()
            launchers.forEach {
                it.start(updateDesc)
            }
        }

    }

    private fun updateApkConfig() {
        val updateDesc = requireNotNull(updateDesc.value).trim()
        val apkDir = requireNotNull(apkDirState.value)
        val newExtension = apkConfig.extension.copy(
            apkDir = apkDir.absolutePath,
            updateDesc = updateDesc
        )
        val configDao = ApkConfigDao()
        configDao.saveApkConfig(apkConfig.copy(extension = newExtension))
    }

    fun getApkDir(): File? {
        return apkConfig.extension.apkDir?.let { File(it) }?.takeIf { it.isDirectory }
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

    private fun selectedLaunchers() = taskLaunchers.filter { selectedChannels.contains(it.name) }

}