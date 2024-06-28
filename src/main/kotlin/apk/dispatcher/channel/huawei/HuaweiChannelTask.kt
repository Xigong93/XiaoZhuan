package apk.dispatcher.channel.huawei

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class HuaweiChannelTask : ChannelTask() {

    override val channelName: String = "华为"

    override val fileNameIdentify: String = "HUAWEI"

    override val paramDefine: List<Param> = listOf(CLIENT_ID, CLIENT_SECRET)

    private val connectClient = HuaweiConnectClient()

    private var clientId = ""

    private var clientSecret = ""

    override fun init(params: Map<Param, String?>) {
        AppLogger.debug(channelName,"参数:$params")
        clientId = params[CLIENT_ID] ?: ""
        clientSecret = params[CLIENT_SECRET] ?: ""
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        connectClient.uploadApk(file, apkInfo, clientId, clientSecret, updateDesc) {
            progress((it * 100).roundToInt())
        }
    }


    companion object {
        private val CLIENT_ID = Param("client_id", desc = "客户端ID")
        private val CLIENT_SECRET = Param("client_secret", desc = "秘钥")
    }

}