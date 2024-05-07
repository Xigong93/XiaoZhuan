package apk.dispatcher.channel

import apk.dispatcher.ApkChannelTask
import java.io.File

class HonourChannelTask : ApkChannelTask() {

    override val channelName: String = "荣耀"

    override val fileNameIdentify: String = "HONOUR"

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