package apk.diapatcher.channel

import apk.diapatcher.ApkChannel
import java.io.File
import java.util.logging.Logger

class MiChannel : ApkChannel() {

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

    override fun performUpload(file: File, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }


}