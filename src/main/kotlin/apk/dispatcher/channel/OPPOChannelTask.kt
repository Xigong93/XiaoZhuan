package apk.dispatcher.channel

import apk.dispatcher.ApkChannelTask
import java.io.File

class OPPOChannelTask : ApkChannelTask() {

    override val channelName: String = "OPPO"

    override val fileNameIdentify: String = "OPPO"

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String?>) {

    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }


}