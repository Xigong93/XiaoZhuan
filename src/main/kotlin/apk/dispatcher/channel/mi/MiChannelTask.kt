package apk.dispatcher.channel.mi

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.util.getApkInfo
import java.io.File
import java.util.logging.Logger
import kotlin.math.roundToInt

class MiChannelTask : ChannelTask() {

    override val channelName: String = "小米"

    private val logger = Logger.getLogger(channelName)

    override val fileNameIdentify: String = "MI"

    private var account = ""
    private var publicKey = ""
    private var privateKey = ""

    override val paramDefine: List<Param> = listOf(ACCOUNT_PARAM, PUBLIC_KEY_PARAM, PRIVATE_KEY_PARAM)

    override fun init(params: Map<Param, String?>) {
        logger.info("参数:$params")
        account = params[ACCOUNT_PARAM] ?: ""
        publicKey = params[PUBLIC_KEY_PARAM] ?: ""
        privateKey = params[PRIVATE_KEY_PARAM] ?: ""
    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {
        val miMarketApi = MiMarketApi(account, publicKey, privateKey)
        val apkInfo = getApkInfo(file)
        val appInfo = miMarketApi.getAppInfo(apkInfo.applicationId)
        logger.info("AppInfo:$appInfo")
        miMarketApi.uploadApk(file, appInfo, updateDesc) {
            progress((it * 100).roundToInt())
        }
    }

    companion object {
        private val ACCOUNT_PARAM = Param("account", desc = "账号(邮箱)")
        private val PUBLIC_KEY_PARAM = Param("publicKey", desc = "公钥", exceptLines = 6)
        private val PRIVATE_KEY_PARAM = Param("privateKey", desc = "私钥")
    }
}