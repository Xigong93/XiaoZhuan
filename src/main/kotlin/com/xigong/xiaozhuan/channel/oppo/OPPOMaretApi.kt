package com.xigong.xiaozhuan.channel.oppo

import com.xigong.xiaozhuan.OkHttpFactory
import com.xigong.xiaozhuan.channel.checkApiSuccess
import com.xigong.xiaozhuan.util.ApkInfo
import com.xigong.xiaozhuan.util.ProgressBody
import com.xigong.xiaozhuan.util.ProgressChange
import com.xigong.xiaozhuan.util.getJsonResult
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.xigong.xiaozhuan.channel.VersionParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class OPPOMaretApi(
    private val clientId: String,
    private val clientSecret: String,
) {

    private val okHttpClient = OkHttpFactory.default()

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
     * 获取App信息
     */
    suspend fun getAppInfo(token: String, packageName: String): OPPOAppInfo = withContext(Dispatchers.IO) {
        val params = mapOf(
            "pkg_name" to packageName
        )
        val requestUrl = getRequestUrl("${DOMAIN}/resource/v1/app/info", params, token, true)
        val request = Request.Builder().url(requestUrl).get().build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("获取App信息")
        OPPOAppInfo(result.get("data").asJsonObject)
    }

    /**
     * 获取上传的url
     */
    suspend fun getUploadUrl(token: String): OPPOUploadUrl = withContext(Dispatchers.IO) {
        val url = "$DOMAIN/resource/v1/upload/get-upload-url"
        val requestUrl = getRequestUrl(url, emptyMap(), token, true)
        val request = Request.Builder().url(requestUrl).get().build()
        val body = okHttpClient.getJsonResult(request)
        body.checkSuccess("获取上传url")
        val data = body.getAsJsonObject("data")
        val uploadUrl = requireNotNull(data.get("upload_url")).asString
        val sign = requireNotNull(data.get("sign")).asString
        OPPOUploadUrl(uploadUrl, sign)
    }


    /**
     * 上传apk
     */
    suspend fun uploadApk(
        uploadUrl: OPPOUploadUrl,
        token: String,
        apkFile: File,
        progressChange: ProgressChange,
    ): OPPOApkResult = withContext(Dispatchers.IO) {
        val params = mapOf(
            "type" to "apk",
            "sign" to uploadUrl.sign,
        )
        val requestUrl = getRequestUrl(uploadUrl.url, params, token, false)
        val apkBody = ProgressBody(
            mediaType = "application/octet-stream".toMediaType(),
            file = apkFile,
            progressChange = progressChange
        )
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM) // 强制指定类型，默认是mixed
            .addFormDataPart("file", apkFile.name, apkBody)
            .addFormDataPart("type", "apk")
            .addFormDataPart("sign", uploadUrl.sign)
            .build()
        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("上传Apk")
        OPPOApkResult(result.get("data").asJsonObject)
    }

    suspend fun submit(
        token: String,
        apkInfo: ApkInfo,
        appInfo: OPPOAppInfo,
        versionParams: VersionParams,
        apkResult: OPPOApkResult
    ) = withContext(Dispatchers.IO) {
        val apkUrl = JsonArray().apply {
            add(JsonObject().apply {
                addProperty("url", apkResult.url)
                addProperty("md5", apkResult.md5)
                addProperty("cpu_code", 0) //多包平台，64 位 CPU 包为 64，32 位 CPU 包为 32，非多包应用为 0
            })
        }
        val onlineTime = versionParams.onlineTime
        val onlineType = if (onlineTime > 0) "2" else "1"

        val params = mutableMapOf(
            "pkg_name" to apkInfo.applicationId,
            "version_code" to apkInfo.versionCode.toString(),
            "apk_url" to apkUrl.toString(),
            "update_desc" to versionParams.updateDesc,
            "online_type" to onlineType, // 1. 审核后立即发布 2. 定时发布
            "second_category_id" to appInfo.secondCategory,
            "third_category_id" to appInfo.thirdCategory,
            "summary" to appInfo.summary,
            "detail_desc" to appInfo.detailDesc,
            "privacy_source_url" to appInfo.privacyUrl,
            "icon_url" to appInfo.iconUrl,
            "pic_url" to appInfo.picUrl,
            "test_desc" to appInfo.testDesc,
            "business_username" to appInfo.businessUsername,
            "business_email" to appInfo.businessEmail,
            "business_mobile" to appInfo.businessMobile,
            // 纸质软著
            "copyright_url" to appInfo.copyrightUrl.ifEmpty {
                appInfo.electronicCertUrl
            },
            // 电子版软著
            "electronic_cert_url" to appInfo.electronicCertUrl,
        )

        if (onlineTime > 0) {
            // 	定时发布时间，online_type=2 时必填，不能早于当前时间
            //格式参考2006-01-02 15:04:05
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val time = format.format(Date(onlineTime))
            params["sche_online_time"] = time
        }
        val body = FormBody.Builder()
            .apply {
                params.forEach { add(it.key, it.value) }
            }
            .build()
        val requestUrl = getRequestUrl("${DOMAIN}/resource/v1/app/upd", params, token, false)
        val request = Request.Builder()
            .url(requestUrl)
            .post(body)
            .build()
        val result = okHttpClient.getJsonResult(request)
        result.checkSuccess("提交版本")
    }

    /**
     * @param paramsAppendQuery 是否将参数添加到url中(仅get请求需要这么做)
     */
    private fun getRequestUrl(
        originUrl: String,
        params: Map<String, String>,
        token: String,
        paramsAppendQuery: Boolean
    ): HttpUrl {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val newParams = params.toMutableMap().apply {
            put("access_token", token)
            put("timestamp", timestamp)
        }
        return originUrl.toHttpUrl().newBuilder()
            .apply {
                if (paramsAppendQuery) {
                    newParams.forEach { setQueryParameter(it.key, it.value) }
                }
            }
            .addQueryParameter("access_token", token)
            .addQueryParameter("timestamp", timestamp)
            .addQueryParameter("api_sign", OPPOApiSigner.sign(clientSecret, newParams))
            .build()
    }

    private fun JsonObject.checkSuccess(action: String) {
        @Suppress("SpellCheckingInspection")
        val code = get("errno").asInt
        val message = get("data")?.asJsonObject?.get("message")?.asString ?: ""
        checkApiSuccess(code, 0, action, message)
    }


    private companion object {
        const val DOMAIN = "https://oop-openapi-cn.heytapmobi.com"
    }
}