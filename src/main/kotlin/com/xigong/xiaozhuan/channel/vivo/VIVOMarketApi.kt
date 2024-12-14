package com.xigong.xiaozhuan.channel.vivo

import com.xigong.xiaozhuan.OkHttpFactory
import com.xigong.xiaozhuan.channel.checkApiSuccess
import com.xigong.xiaozhuan.util.FileUtil
import com.xigong.xiaozhuan.util.ProgressBody
import com.xigong.xiaozhuan.util.ProgressChange
import com.xigong.xiaozhuan.util.getJsonResult
import com.google.gson.JsonObject
import com.xigong.xiaozhuan.channel.VersionParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
        val apkBody = ProgressBody(
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
    suspend fun submit(apkResult: VIVOApkResult, versionParams: VersionParams, appInfo: VIVOAppInfo) =
        withContext(Dispatchers.IO) {
            val onlineTime = versionParams.onlineTime
            // 立即上架:1,定时上架:2
            val onlineType = if (onlineTime > 0) "2" else "1"
            val params = mutableMapOf(
                "packageName" to apkResult.packageName,
                "versionCode" to apkResult.versionCode.toString(),
                "apk" to apkResult.serialnumber,
                "fileMd5" to apkResult.fileMd5,
                "onlineType" to onlineType,
                "updateDesc" to versionParams.updateDesc,
            )
            if (onlineTime > 0) {
                // 上架时间，若onlineType   = 2，上架时间必填。格式：yyyy-MM-dd   HH:mm:ss
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val time = format.format(Date(onlineTime))
                params["scheOnlineTime"] = time
            }
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

    private fun JsonObject.checkSuccess(action: String) {
        val code = get("code").asInt
        val subCode = get("subCode").asString.toIntOrNull() ?: -1

        val msg = get("msg")?.asString ?: "未知"
        // VIVO 的接口，并没有提供message的字段
        val message = "请浏览 https://dev.vivo.com.cn/documentCenter/doc/330 对照code码查看信息"
        checkApiSuccess(code, 0, action, msg)
        checkApiSuccess(subCode, 0, action, msg)
    }

    private companion object {
        const val DEBUG_DOMAIN = "https://sandbox-developer-api.vivo.com.cn/router/rest"
        const val RELEASE_DOMAIN = "https://developer-api.vivo.com.cn/router/rest"

        const val DOMAIN = RELEASE_DOMAIN
    }
}