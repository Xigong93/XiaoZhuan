package apk.dispatcher.channel

import apk.dispatcher.ApkChannelTask
import apk.dispatcher.util.defaultLogger
import kotlinx.coroutines.delay
import java.io.File

class MockChannelTask(
    override val channelName: String,
    override val fileNameIdentify: String
) : ApkChannelTask() {

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String?>) {

    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {
        defaultLogger.info("Mock ${channelName},开始上传")
        repeat(100) {
            delay(100)
            progress(it)
        }
        defaultLogger.info("Mock ${channelName},上传完成")
    }

}