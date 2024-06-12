package apk.dispatcher.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import apk.dispatcher.ApkChannelTask
import apk.dispatcher.ApkConfig
import apk.dispatcher.util.PathUtil
import java.io.File

class TaskLauncher(
    private val apkConfig: ApkConfig,
    private val task: ApkChannelTask
) {

    val name = task.channelName

    private val apkFileState: MutableState<File?> = mutableStateOf(null)

    private val channelState: MutableState<ChannelState?> = mutableStateOf(null)

    private val stateListener = object : ApkChannelTask.StateListener {
        override fun onStart() {
            updateState(ChannelState.Uploading(0))
        }

        override fun onProgress(progress: Int) {
            updateState(ChannelState.Uploading(progress))
        }

        override fun onSuccess() {
            updateState(ChannelState.Success)
        }

        override fun onError(exception: Exception) {
            updateState(ChannelState.Error("上传失败，请重试"))
        }
    }

    fun selectFile(apkDir: File) {
        val apkFile = if (apkConfig.extension.enableChannel) {
            findApkFile(apkDir)
        } else {
            apkDir
        }
        apkFileState.value = apkFile
    }

    fun prepare() {
        updateState(ChannelState.Waiting)
    }


    suspend fun start(updateDesc: String) {
        val apkFile = requireNotNull(apkFileState.value)
        task.init(getParams())
        task.setListener(stateListener)
        task.startUpload(apkFile, updateDesc)
    }

    private fun findApkFile(apkDir: File): File {
        val apks = PathUtil.listApkFile(apkDir)
        val fileId = apkConfig.getChannel(name)
            ?.getParam(ApkChannelTask.FILE_NAME_IDENTIFY)
            ?.value
            ?: task.fileNameIdentify
        val file = apks.firstOrNull { it.name.contains(fileId, true) }
        return checkNotNull(file) { "找不到文件名中包含:${fileId}的文件" }
    }

    fun getApkFileState(): State<File?> = apkFileState

    fun getChannelState(): State<ChannelState?> = channelState

    private fun updateState(newState: ChannelState) {
        channelState.value = newState
    }

    private fun getParams(): Map<ApkChannelTask.Param, String?> {
        val channelParams = apkConfig.channels.associateBy { it.name }
        val saveParams = channelParams[task.channelName]?.params
        val getParam = { p: ApkChannelTask.Param -> saveParams?.firstOrNull { it.name == p.name }?.value }
        return task.getParams().associateWith { p -> getParam(p) }
    }
}