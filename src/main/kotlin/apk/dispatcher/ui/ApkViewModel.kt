package apk.dispatcher.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import apk.dispatcher.ApkChannelRegistry
import apk.dispatcher.ApkChannelTask
import apk.dispatcher.ApkConfig
import apk.dispatcher.util.ApkInfo
import apk.dispatcher.util.PathUtil
import apk.dispatcher.util.getApkInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ApkViewModel(
    private val apkConfig: ApkConfig
) {

    private val mainScope = GlobalScope

    val updateDesc = mutableStateOf<String>("")

    private val apkDirState = mutableStateOf<File?>(null)

    private val apkInfoState = mutableStateOf<ApkInfo?>(null)

    val channels: List<ApkChannelTask> = ApkChannelRegistry.channels

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

    fun selectedApkDir(dir: File) {
        apkDirState.value = dir
        mainScope.launch {
            try {
                val apkFile = PathUtil.listApkFile(dir).first()
                val launchers = selectedLaunchers()
                launchers.forEach { it.selectFile(dir) }
                apkInfoState.value = getApkInfo(apkFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    fun startDispatch() {
        mainScope.launch {
            val launchers = selectedLaunchers()
            val apkFile = requireNotNull(apkDirState.value)
            val updateDesc = requireNotNull(updateDesc.value)
            launchers.forEach {
                it.prepare()
            }
            launchers.forEach {
                it.start(apkFile, updateDesc)
            }
        }

    }

    private fun selectedLaunchers() = taskLaunchers.filter { selectedChannels.contains(it.name) }

}