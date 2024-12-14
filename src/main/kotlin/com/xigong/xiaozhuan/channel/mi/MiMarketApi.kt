package com.xigong.xiaozhuan.channel.mi

import com.xigong.xiaozhuan.OkHttpFactory
import com.xigong.xiaozhuan.channel.checkApiSuccess
import com.xigong.xiaozhuan.util.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import org.apache.commons.codec.digest.DigestUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*


/**
 * 小米应用市场Api
 * @param publicKey 公钥
 * @param privateKey 私钥
 */
class MiMarketApi(
    private val miAccount: String,
    private val publicKey: String,
    private val privateKey: String
) {
    private val httpClient = OkHttpFactory.default()

    /**
     * 获取App信息
     * @param miAccount 小米应用市场账号（邮箱)
     * @param packageName 应用包名
     */
    suspend fun getAppInfo(packageName: String): MiAppInfoResp =
        withContext(Dispatchers.IO) {
            val requestData = JSONObject().apply {
                put("userName", miAccount)
                put("packageName", packageName)
            }
            val sig = JSONObject().apply {
                put("password", privateKey)
                put("sig", JSONArray().apply {
                    put(JSONObject().apply {
                        put("name", "RequestData")
                        put("hash", DigestUtils.md5Hex(requestData.toString()))
                    })
                })
            }
            val body = FormBody.Builder()
                .add("RequestData", requestData.toString())
                .add("SIG", MiApiSigner.encrypt(sig.toString(), publicKey))
                .build()
            val request = Request.Builder().url(QUERY).post(body).build()
            val response = httpClient.getTextResult(request)
            val jsonResult = JsonParser.parseString(response).asJsonObject
            jsonResult.checkSuccess("获取App信息")
            val infoResp = MiAppInfoResp.adapter.fromJson(response)
            checkNotNull(infoResp)
            infoResp
        }

    /**
     * 上传Apk
     */
    suspend fun uploadApk(
        apkFile: File,
        appInfo: MiAppInfoResp.MiAppInfo,
        updateDesc: String,
        onlineTime: Long,
        progressChange: ProgressChange
    ) = withContext(Dispatchers.IO) {
        val requestData = JSONObject().apply {
            put("userName", miAccount)
            put("synchroType", 1)// 0：新增app; 1：更新app; 2：app信息修改
            put("appInfo", JSONObject().apply {
                put("appName", appInfo.appName)
                put("packageName", appInfo.packageName)
                put("updateDesc", updateDesc)
                if (onlineTime > 0) {
                    put("onlineTime", onlineTime)// 上线时间，毫秒时间戳
                }
            })
        }

        val sig = JSONObject().apply {
            put("password", privateKey)
            put("sig", JSONArray().apply {
                put(JSONObject().apply {
                    put("name", "RequestData")
                    put("hash", DigestUtils.md5Hex(requestData.toString()))
                })
                put(JSONObject().apply {
                    put("name", "apk")
                    put("hash", FileUtil.getFileMD5(apkFile))
                })
            })
        }
        val apkBody = ProgressBody(
            mediaType = "application/octet-stream".toMediaType(),
            file = apkFile,
            progressChange = progressChange
        )
        val body = MultipartBody.Builder()
            .addFormDataPart("apk", "", apkBody)
            .addFormDataPart("RequestData", requestData.toString())
            .addFormDataPart("SIG", MiApiSigner.encrypt(sig.toString(), publicKey))
            .build()
        val request = Request.Builder().url(PUSH).post(body).build()
        val result = httpClient.getJsonResult(request)
        result.checkSuccess("上传Apk")
    }

    private fun JsonObject.checkSuccess(action: String) {
        val code = get("result").asInt
        val message = get("message").asString
        checkApiSuccess(code, 0, action, message)
    }


    private companion object {
        /**
         * 自动发布接口域名
         */

        const val DOMAIN: String = "http://api.developer.xiaomi.com/devupload"

        /**
         * 推送普通apk Url前缀
         */

        const val PUSH: String = "$DOMAIN/dev/push"


        /**
         * 查询app状态的Url前缀
         */

        const val QUERY: String = "$DOMAIN/dev/query"


    }
}