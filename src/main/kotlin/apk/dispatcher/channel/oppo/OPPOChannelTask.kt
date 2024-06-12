package apk.dispatcher.channel.oppo

import apk.dispatcher.ApkChannelTask
import apk.dispatcher.util.defaultLogger
import apk.dispatcher.util.getApkInfo
import java.io.File
import kotlin.math.roundToInt

class OPPOChannelTask : ApkChannelTask() {

    override val channelName: String = "OPPO"

    override val fileNameIdentify: String = "OPPO"

    override val paramDefine: List<Param> = listOf(CLIENT_ID_PARAM, CLIENT_SECRET_PARAM)

    private var clientId = ""

    private var clientSecret = ""

    override fun init(params: Map<Param, String?>) {
        clientId = params[CLIENT_ID_PARAM] ?: ""
        clientSecret = params[CLIENT_SECRET_PARAM] ?: ""
    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {

        val maretApi = OPPOMaretApi(clientId, clientSecret)
        val apkInfo = getApkInfo(file)
        val token = maretApi.getToken()
        defaultLogger.info("token=$token")
        val appInfo = maretApi.getAppInfo(token, apkInfo.applicationId)
        val uploadUrl = maretApi.getUploadUrl(token)
        defaultLogger.info("uploadUrl=$uploadUrl")
        val apkResult = maretApi.uploadApk(uploadUrl, token, file) {
            progress((it * 100).roundToInt())
        }
        defaultLogger.info("apkResult=$apkResult")
        maretApi.submit(token, apkInfo, appInfo, updateDesc, apkResult)
    }

    companion object {
        private val CLIENT_ID_PARAM = Param("client_id")

        private val CLIENT_SECRET_PARAM = Param("client_secret")
    }

}