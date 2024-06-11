package apk.dispatcher.channel.vivo

import apk.dispatcher.ApkChannelTask
import java.io.File

class VIVOChannelTask : ApkChannelTask() {

    override val channelName: String = "VIVO"

    override val fileNameIdentify: String = "VIVO"

    override val paramDefine: List<Param> = listOf(ACCESS_KEY, ACCESS_SECRET)

    private var accessKey = ""

    private var accessSecret = ""

    override fun init(params: Map<Param, String?>) {
        accessKey = params[ACCESS_KEY] ?: ""
        accessSecret = params[ACCESS_SECRET] ?: ""
    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {

    }

    companion object {
        private val ACCESS_KEY = Param("access_key")

        private val ACCESS_SECRET = Param("access_secret")
    }


}