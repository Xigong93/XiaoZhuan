package apk.dispatcher.channel

import apk.dispatcher.ApkChannelTask
import java.io.File
import java.util.logging.Logger

class MiChannelTask : ApkChannelTask() {

    override val channelName: String = "小米"

    private val logger = Logger.getLogger(channelName)

    override val fileNameIdentify: String = "MI"

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String?>) {
        logger.info("参数:$params")
    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }

}