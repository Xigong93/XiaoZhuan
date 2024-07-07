package apk.dispatcher.channel.oppo

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.MarketInfo
import apk.dispatcher.util.ApkInfo
import java.io.File

class OPPOChannelTask : ChannelTask() {

    override val channelName: String = "OPPO"

    override val fileNameIdentify: String = "OPPO"

    override val paramDefine: List<Param> = listOf(CLIENT_ID_PARAM, CLIENT_SECRET_PARAM)

    private var marketClient: OPPOMarketClient? = null

    override fun init(params: Map<Param, String?>) {
        val clientId = params[CLIENT_ID_PARAM] ?: ""
        val clientSecret = params[CLIENT_SECRET_PARAM] ?: ""
        marketClient = OPPOMarketClient(clientId, clientSecret)
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        requireNotNull(marketClient).submit(file, apkInfo, updateDesc, progress)
    }

    override suspend fun getMarketState(applicationId: String): MarketInfo {
        val appInfo = requireNotNull(marketClient).getAppInfo(applicationId)
        return appInfo.toMarketState()
    }

    companion object {
        private val CLIENT_ID_PARAM = Param("client_id")

        private val CLIENT_SECRET_PARAM = Param("client_secret")
    }

}