package apk.dispatcher.channel.vivo

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.MarketState
import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import java.io.File

class VIVOChannelTask : ChannelTask() {

    override val channelName: String = "VIVO"

    override val fileNameIdentify: String = "VIVO"

    override val paramDefine: List<Param> = listOf(ACCESS_KEY, ACCESS_SECRET)

    private var marketClient: VIVOMarketClient? = null

    override fun init(params: Map<Param, String?>) {
        val accessKey = params[ACCESS_KEY] ?: ""
        val accessSecret = params[ACCESS_SECRET] ?: ""
        marketClient = VIVOMarketClient(accessKey, accessSecret)
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        requireNotNull(marketClient).submit(file, apkInfo, updateDesc, progress)

    }

    override suspend fun getMarketState(applicationId: String): MarketState {
        val appDetail = requireNotNull(marketClient).getAppInfo(applicationId)
        return appDetail.toMarketState()
    }

    companion object {
        private val ACCESS_KEY = Param("access_key")

        private val ACCESS_SECRET = Param("access_secret")
    }


}