package apk.dispatcher.channel.vivo

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.MarketState
import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class VIVOChannelTask : ChannelTask() {

    override val channelName: String = "VIVO"

    override val fileNameIdentify: String = "VIVO"

    override val paramDefine: List<Param> = listOf(ACCESS_KEY, ACCESS_SECRET)

    private var accessKey = ""

    private var accessSecret = ""

    override fun init(params: Map<Param, String?>) {
        AppLogger.debug(channelName, "参数:$params")
        accessKey = params[ACCESS_KEY] ?: ""
        accessSecret = params[ACCESS_SECRET] ?: ""
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {
        val api = VIVOMarketApi(accessKey, accessSecret)
        val appDetail = api.getAppInfo(apkInfo.applicationId)
        AppLogger.info(channelName, "查看App详情:${appDetail}")
        val apkResult = api.uploadApk(file, apkInfo.applicationId) {
            progress((it * 100).roundToInt())
        }
        AppLogger.info(channelName, "上传apk结果:${apkResult}")
        api.submit(apkResult, updateDesc, appDetail)

    }

    override suspend fun getMarketState(applicationId: String): MarketState {
        val api = VIVOMarketApi(accessKey, accessSecret)
        val appDetail = api.getAppInfo(applicationId)
        AppLogger.info(channelName, "查看App详情:${appDetail}")
        return appDetail.toMarketState()
    }

    companion object {
        private val ACCESS_KEY = Param("access_key")

        private val ACCESS_SECRET = Param("access_secret")
    }


}