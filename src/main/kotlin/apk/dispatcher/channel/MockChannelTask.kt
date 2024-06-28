package apk.dispatcher.channel

import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import kotlinx.coroutines.delay
import java.io.File

class MockChannelTask(
    override val channelName: String,
    override val fileNameIdentify: String
) : ChannelTask() {

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String?>) {

    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        AppLogger.info(LOG_TAG, "Mock ${channelName},开始上传")
        repeat(100) {
            delay(100)
            progress(it)
        }
        AppLogger.info(LOG_TAG, "Mock ${channelName},上传完成")
    }

    companion object {
        private const val LOG_TAG = "模拟上传"
    }
}