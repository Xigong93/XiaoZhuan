package apk.dispatcher.channel.oppo

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.channel.MarketState
import apk.dispatcher.log.AppLogger
import apk.dispatcher.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class OPPOChannelTask : ChannelTask() {

    override val channelName: String = "OPPO"

    override val fileNameIdentify: String = "OPPO"

    override val paramDefine: List<Param> = listOf(CLIENT_ID_PARAM, CLIENT_SECRET_PARAM)

    private var clientId = ""

    private var clientSecret = ""

    override fun init(params: Map<Param, String?>) {
        AppLogger.debug(channelName, "参数:$params")
        clientId = params[CLIENT_ID_PARAM] ?: ""
        clientSecret = params[CLIENT_SECRET_PARAM] ?: ""
    }

    override suspend fun performUpload(file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit) {

        val maretApi = OPPOMaretApi(clientId, clientSecret)
        val token = maretApi.getToken()
        AppLogger.info(channelName, "token=$token")
        val appInfo = maretApi.getAppInfo(token, apkInfo.applicationId)
        val uploadUrl = maretApi.getUploadUrl(token)
        AppLogger.info(channelName, "uploadUrl=$uploadUrl")
        val apkResult = maretApi.uploadApk(uploadUrl, token, file) {
            progress((it * 100).roundToInt())
        }
        AppLogger.info(channelName, "apkResult=$apkResult")
        maretApi.submit(token, apkInfo, appInfo, updateDesc, apkResult)
    }

    override suspend fun getMarketState(applicationId: String): MarketState {
        val maretApi = OPPOMaretApi(clientId, clientSecret)
        val token = maretApi.getToken()
        AppLogger.info(channelName, "token=$token")
        val appInfo = maretApi.getAppInfo(token, applicationId)
        AppLogger.info(channelName, "appInfo=$appInfo")
        return appInfo.toMarketState()
    }

    companion object {
        private val CLIENT_ID_PARAM = Param("client_id")

        private val CLIENT_SECRET_PARAM = Param("client_secret")
    }

}