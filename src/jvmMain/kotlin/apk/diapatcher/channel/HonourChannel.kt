package apk.diapatcher.channel

import apk.diapatcher.ApkChannel
import java.io.File

class HonourChannel : ApkChannel() {

    override val channelName: String = "荣耀"

    override val fileNameIdentify: String = "HONOUR"

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String?>) {

    }

    override fun performUpload(file: File, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }


}