package apk.dispatcher.huawei

import apk.dispatcher.OkhttpFactory
import apk.dispatcher.RetrofitFactory
import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.ProgressRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File

fun HuaweiConnectApi(): HuaweiConnectApi {
    return RetrofitFactory.create("https://www.huawei.com/auth/agc/publish/")
}

/**
 * 华为提供的Api
 * https://developer.huawei.com/consumer/cn/doc/AppGallery-connect-Guides/agcapi-getstarted-0000001111845114
 */
interface HuaweiConnectApi {


    /**
     * 获取token
     */
    @POST("https://connect-api.cloud.huawei.com/api/oauth2/v1/token")
    suspend fun getToken(
        @Body params: HWTokenParams
    ): HWTokenResp


    /**
     * 通过包名获取AppId
     */
    @POST("oauth2/v1/token")
    suspend fun getAppId(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("packageName") packageName: String,
    ): HWAppIdResp

    /**
     * 获取文件上传地址
     */
    @POST("v2/upload-url/for-obs")
    suspend fun getUploadUrl(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Query("fileName") fileName: String,
        @Query("contentLength") contentLength: Long,
    ): HWUploadUrlResp

    /**
     * 上传文件
     */
    suspend fun uploadFile(
        file: File,
        url: HWUploadUrlResp.UploadUrl,
        progressChange: ProgressChange
    ): Unit = withContext(Dispatchers.IO) {
        val client = OkhttpFactory.default()
        val headers = Headers.Builder()
        url.headers.forEach { (k, v) ->
            headers.add(k, v)
        }
        val contentType = "application/octet-stream".toMediaType()
        val body = ProgressRequestBody(
            contentType, file, progressChange
        )
        val request = Request.Builder()
            .url(url.url)
            .headers(headers.build())
            .put(body)
            .build()
        val resp = client.newCall(request).execute()
        check(resp.isSuccessful) { "http error ${resp.code}" }
    }


    /**
     * 更新版本描述
     */
    @POST("v2/app-language-info")
    suspend fun updateVersionDesc(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Body
        versionDesc: HWVersionDesc
    ): HWResp


    /**
     * 提交审核
     */
    @POST("v2/app-submit")
    suspend fun submit(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String
    ): HWResp

}