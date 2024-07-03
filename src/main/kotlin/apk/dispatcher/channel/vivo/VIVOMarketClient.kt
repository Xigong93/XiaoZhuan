package apk.dispatcher.channel.vivo

import apk.dispatcher.log.AppLogger
import apk.dispatcher.log.action
import apk.dispatcher.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class VIVOMarketClient(
    accessKey: String,
    accessSecret: String,
) {
    private val marketApi = VIVOMarketApi(accessKey, accessSecret)

    suspend fun submit(
        file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit
    ) = AppLogger.action(LOG_TAG, "提交新版本") {
        val appInfo = getAppInfo(apkInfo.applicationId)
        val apkResult = uploadApk(file, apkInfo, progress)
        performSubmit(apkResult, updateDesc, appInfo)
    }

    suspend fun getAppInfo(
        applicationId: String
    ): VIVOAppInfo = AppLogger.action(LOG_TAG, "获取App信息") {
        marketApi.getAppInfo(applicationId)
    }

    private suspend fun uploadApk(
        file: File, apkInfo: ApkInfo, progress: (Int) -> Unit
    ): VIVOApkResult = AppLogger.action(LOG_TAG, "上传Apk文件") {
        marketApi.uploadApk(file, apkInfo.applicationId) {
            progress((it * 100).roundToInt())
        }
    }

    private suspend fun performSubmit(
        apkResult: VIVOApkResult, updateDesc: String, appInfo: VIVOAppInfo
    ): Unit = AppLogger.action(LOG_TAG, "提交审核") {
        marketApi.submit(apkResult, updateDesc, appInfo)

    }

    companion object {
        private const val LOG_TAG = "VIVO应用市场Api"
    }
}