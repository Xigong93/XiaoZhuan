package apk.dispatcher.channel.mi

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.MarketState
import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class MiChannelTask : ChannelTask() {

    override val channelName: String = "小米"

    override val fileNameIdentify: String = "MI"

    private var account = ""
    private var publicKey = ""
    private var privateKey = ""

    override val paramDefine: List<Param> = listOf(ACCOUNT_PARAM, PUBLIC_KEY_PARAM, PRIVATE_KEY_PARAM)

    override fun init(params: Map<Param, String?>) {
        AppLogger.debug(channelName, "参数:$params")
        account = params[ACCOUNT_PARAM] ?: ""
        publicKey = params[PUBLIC_KEY_PARAM] ?: ""
        privateKey = params[PRIVATE_KEY_PARAM] ?: ""
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        val miMarketApi = MiMarketApi(account, publicKey, privateKey)
        val appInfo = miMarketApi.getAppInfo(apkInfo.applicationId)
        AppLogger.info(channelName, "AppInfo:$appInfo")
        miMarketApi.uploadApk(file, appInfo.packageInfo, updateDesc) {
            progress((it * 100).roundToInt())
        }
    }

    override suspend fun getMarketState(applicationId: String): MarketState {
        val miMarketApi = MiMarketApi(account, publicKey, privateKey)
        val appInfo = miMarketApi.getAppInfo(applicationId)
        AppLogger.info(channelName, "AppInfo:$appInfo")
        return appInfo.toMarketState()
    }

    companion object {
        private val ACCOUNT_PARAM = Param("account", desc = "账号(邮箱)")
        private val PUBLIC_KEY_PARAM = Param("publicKey", desc = "公钥", type = ParmaType.TextFile("cer"))
        private val PRIVATE_KEY_PARAM = Param("privateKey", desc = "私钥")
    }
}