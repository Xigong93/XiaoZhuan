package apk.dispatcher.huawei

import apk.dispatcher.ApkChannelTask
import java.io.File
import java.util.logging.Logger
import kotlin.math.roundToInt

class HuaweiChannelTask : ApkChannelTask() {

    override val channelName: String = "华为"

    private val logger = Logger.getLogger(channelName)

    override val fileNameIdentify: String = "HUAWEI"

    override val paramDefine: List<Param> = listOf(CLIENT_ID, CLIENT_SECRET)

    private val connectClient = HuaweiConnectClient()

    private var clientId = ""
    private var clientSecret = ""

    override fun init(params: Map<Param, String?>) {
        logger.info("参数:$params")
        clientId = params[CLIENT_ID] ?: ""
        clientSecret = params[CLIENT_SECRET] ?: ""
    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {
        connectClient.uploadApk(file, updateDesc, clientId, clientSecret) {
            progress((it * 100).roundToInt())
        }
    }


    companion object {
        private val CLIENT_ID = Param("client_id", desc = "客户端ID")
        private val CLIENT_SECRET = Param("client_secret", desc = "秘钥")
    }

}