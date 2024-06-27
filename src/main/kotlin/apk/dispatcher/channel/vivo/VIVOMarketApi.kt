package apk.dispatcher.channel.vivo

import apk.dispatcher.OkHttpFactory
import apk.dispatcher.util.FileUtil
import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.ProgressRequestBody
import apk.dispatcher.util.getJsonResult
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.File

class VIVOMarketApi(
    private val accessKey: String,
    private val accessSecret: String,
) {
    private val okHttpClient = OkHttpFactory.default()

    suspend fun getAppInfo(packageName: String): VIVOAppInfo = withContext(Dispatchers.IO) {
        val params = mapOf(
            "packageName" to packageName,
        )
        val requestUrl = getRequestUrl("app.query.details", params)
        val request = Request.Builder()
            .url(requestUrl)
            .get()
            .build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("查询应用详情")
        VIVOAppInfo(result.get("data").asJsonObject)
    }

    /**
     * 上传apk
     */
    suspend fun uploadApk(
        apkFile: File, packageName: String, progressChange: ProgressChange
    ): VIVOApkResult = withContext(Dispatchers.IO) {
        val params = mapOf(
            "packageName" to packageName,
            "fileMd5" to FileUtil.getFileMD5(apkFile)
        )
        val apkBody = ProgressRequestBody(
            mediaType = "application/octet-stream".toMediaType(),
            file = apkFile,
            progressChange = progressChange
        )
        val requestBody = MultipartBody.Builder()
            .addFormDataPart("file", apkFile.name, apkBody)
            .build()
        val requestUrl = getRequestUrl("app.upload.apk.app", params)
        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("上传apk")
        VIVOApkResult(result.get("data").asJsonObject)
    }

    /**
     * 提交更新
     */
    suspend fun submit(apkResult: VIVOApkResult, updateDesc: String,appInfo: VIVOAppInfo) = withContext(Dispatchers.IO) {
        val params = mapOf(
            "packageName" to apkResult.packageName,
            "versionCode" to apkResult.versionCode.toString(),
            "apk" to apkResult.serialnumber,
            "fileMd5" to apkResult.fileMd5,
            "onlineType" to appInfo.onlineType.toString(),
            "updateDesc" to updateDesc,
        )
        val requestUrl = getRequestUrl("app.sync.update.app", params)
        val request = Request.Builder()
            .url(requestUrl)
            .get()
            .build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("提交更新")
    }


    private fun getRequestUrl(method: String, params: Map<String, String>): HttpUrl {
        val signParams = VIVOApiSigner.getSignParams(accessKey, accessSecret, method, params)
        return DOMAIN.toHttpUrl().newBuilder()
            .apply {
                signParams.forEach { addQueryParameter(it.key, it.value) }
            }
            .build()
    }

    private fun JsonObject.checkSuccess(what: String) {
        val code = get("code").asInt
        val subCode = get("subCode").asString
        check(code == 0 && subCode == "0") { "${what}失败,${this}" }
    }

    private companion object {
        const val DEBUG_DOMAIN = "https://sandbox-developer-api.vivo.com.cn/router/rest"
        const val RELEASE_DOMAIN = "https://developer-api.vivo.com.cn/router/rest"

        val DOMAIN = /*if (BuildConfig.DEBUG) DEBUG_DOMAIN else*/ RELEASE_DOMAIN
    }
}