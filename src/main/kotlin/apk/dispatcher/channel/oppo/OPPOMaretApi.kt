package apk.dispatcher.channel.oppo

import apk.dispatcher.OkhttpFactory
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

class OPPOMaretApi(
    private val clientId: String,
    private val clientSecret: String,
) {

    private val okHttpClient = OkhttpFactory.default()

    /**
     * 获取token
     */
    suspend fun getToken(): String = withContext(Dispatchers.IO) {
        val url = "$DOMAIN/developer/v1/token?client_id=$clientId&client_secret=$clientSecret"
        val request = Request.Builder().url(url).get().build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("获取token")
        val data = result.get("data").asJsonObject
        data.get("access_token").asString
    }

    /**
     * 获取上传的url
     */
    suspend fun getUploadUrl(token: String): OPPOUploadUrl = withContext(Dispatchers.IO) {
        val url = "$DOMAIN/resource/v1/upload/get-upload-url"
        val requestUrl = getRequestUrl(url, emptyMap(), token)
        val request = Request.Builder().url(requestUrl).get().build()
        val body = okHttpClient.getJsonResult(request)
        body.checkSuccess("获取上传url")
        val data = body.getAsJsonObject("data")
        val uploadUrl = requireNotNull(data.get("upload_url")).asString
        val sign = requireNotNull(data.get("sign")).asString
        OPPOUploadUrl(uploadUrl, sign)
    }


    suspend fun uploadApk(
        uploadUrl: OPPOUploadUrl,
        token: String,
        apkFile: File,
        progressChange: ProgressChange,
    ): JsonObject = withContext(Dispatchers.IO) {
        val params = mapOf(
            "type" to "apk",
            "sign" to uploadUrl.sign,
        )
        val requestUrl = getRequestUrl(uploadUrl.url, params, token)
        val apkBody = ProgressRequestBody(
            mediaType = "application/octet-stream".toMediaType(),
            file = apkFile,
            progressChange = progressChange
        )
        val requestBody = MultipartBody.Builder()
            .addFormDataPart("file", apkFile.name, apkBody)
            .build()
        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("上传Apk")
        result
    }

    private fun getRequestUrl(originUrl: String, params: Map<String, String>, token: String): HttpUrl {
        val newParams = params.toMutableMap().apply {
            put("access_token", token)
            put("timestamp", (System.currentTimeMillis() / 1000).toString())
        }
        return originUrl.toHttpUrl().newBuilder()
            .apply { newParams.forEach { addQueryParameter(it.key, it.value) } }
            .addQueryParameter("api_sign", OPPOApiSigner.sign(clientSecret, newParams))
            .build()
    }

    private fun JsonObject.checkSuccess(what: String) {
        val code = get("errno").asInt
        check(code == 0) { "${what}失败,${this}" }
    }


    private companion object {
        const val DOMAIN = "https://oop-openapi-cn.heytapmobi.com"
    }
}