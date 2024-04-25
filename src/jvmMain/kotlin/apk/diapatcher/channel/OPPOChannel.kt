package apk.diapatcher.channel

import apk.diapatcher.ApkChannel
import java.io.File

class OPPOChannel : ApkChannel() {

    override val channelName: String = "OPPO"

    override val fileNameIdentify: String = "OPPO"

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String>) {

    }

    override fun performUpload(file: File, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }


}