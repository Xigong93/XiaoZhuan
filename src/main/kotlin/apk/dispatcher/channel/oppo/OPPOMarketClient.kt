package apk.dispatcher.channel.oppo

import apk.dispatcher.log.AppLogger
import apk.dispatcher.log.action
import apk.dispatcher.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class OPPOMarketClient(
    clientId: String,
    clientSecret: String,
) {
    private val marketApi = OPPOMaretApi(clientId, clientSecret)

    suspend fun submit(
        file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit
    ): Unit = AppLogger.action(LOG_TAG, "提交新版本") {
        val token = getToken()
        val appInfo = getAppInfo(token, apkInfo.applicationId)
        val uploadUrl = getUploadUrl(token)
        val apkResult = uploadApk(uploadUrl, token, file, progress)
        performSubmit(token, apkInfo, appInfo, updateDesc, apkResult)
    }

    suspend fun getAppInfo(
        applicationId: String
    ): OPPOAppInfo = AppLogger.action(LOG_TAG, "获取审核状态") {
        val token = getToken()
        getAppInfo(token, applicationId)
    }

    private suspend fun getToken(): String = AppLogger.action(LOG_TAG, "获取token") {
        marketApi.getToken()
    }

    private suspend fun getAppInfo(
        token: String, applicationId: String
    ): OPPOAppInfo = AppLogger.action(LOG_TAG, "获取App信息") {
        marketApi.getAppInfo(token, applicationId)
    }

    private suspend fun getUploadUrl(
        token: String
    ): OPPOUploadUrl = AppLogger.action(LOG_TAG, "获取Apk上传地址") {
        marketApi.getUploadUrl(token)
    }


    private suspend fun uploadApk(
        uploadUrl: OPPOUploadUrl, token: String, file: File, progress: (Int) -> Unit
    ): OPPOApkResult = AppLogger.action(LOG_TAG, "上传Apk文件") {
        marketApi.uploadApk(uploadUrl, token, file) {
            progress((it * 100).roundToInt())
        }
    }

    private suspend fun performSubmit(
        token: String,
        apkInfo: ApkInfo,
        appInfo: OPPOAppInfo,
        updateDesc: String,
        apkResult: OPPOApkResult
    ): Unit = AppLogger.action(LOG_TAG, "提交审核") {
        marketApi.submit(token, apkInfo, appInfo, updateDesc, apkResult)
    }

    companion object {
        private const val LOG_TAG = "OPPO应用市场Api"
    }
}