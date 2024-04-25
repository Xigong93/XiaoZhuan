package apk.diapatcher.channel

import apk.diapatcher.ApkChannel
import java.io.File

class VIVOChannel : ApkChannel() {

    override val channelName: String = "VIVO"

    override val fileNameIdentify: String = "VIVO"

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