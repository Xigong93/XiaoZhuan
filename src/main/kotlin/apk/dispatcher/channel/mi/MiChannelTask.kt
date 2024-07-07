package apk.dispatcher.channel.mi

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.MarketInfo
import apk.dispatcher.util.ApkInfo
import java.io.File

class MiChannelTask : ChannelTask() {

    override val channelName: String = "小米"

    override val fileNameIdentify: String = "MI"

    private var marketClient: MiMarketClient? = null

    override val paramDefine: List<Param> = listOf(ACCOUNT_PARAM, PUBLIC_KEY_PARAM, PRIVATE_KEY_PARAM)

    override fun init(params: Map<Param, String?>) {
        val account = params[ACCOUNT_PARAM] ?: ""
        val publicKey = params[PUBLIC_KEY_PARAM] ?: ""
        val privateKey = params[PRIVATE_KEY_PARAM] ?: ""
        marketClient = MiMarketClient(account, publicKey, privateKey)
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        requireNotNull(marketClient).submit(file, apkInfo, updateDesc, progress)
    }

    override suspend fun getMarketState(applicationId: String): MarketInfo {
        val appInfo = requireNotNull(marketClient).getAppInfo(applicationId)
        return appInfo.toMarketState()
    }

    companion object {
        private val ACCOUNT_PARAM = Param("account", desc = "账号(邮箱)")
        private val PUBLIC_KEY_PARAM = Param("publicKey", desc = "公钥", type = ParmaType.TextFile("cer"))
        private val PRIVATE_KEY_PARAM = Param("privateKey", desc = "私钥")
    }
}