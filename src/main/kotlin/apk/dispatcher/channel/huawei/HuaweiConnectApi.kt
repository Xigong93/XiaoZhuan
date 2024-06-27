package apk.dispatcher.channel.huawei

import apk.dispatcher.OkHttpFactory
import apk.dispatcher.RetrofitFactory
import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.ProgressRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.io.File

fun HuaweiConnectApi(): HuaweiConnectApi {
    return RetrofitFactory.create("https://connect-api.cloud.huawei.com/")
}

/**
 * 华为提供的Api
 * https://developer.huawei.com/consumer/cn/doc/AppGallery-connect-Guides/agcapi-getstarted-0000001111845114
 */
interface HuaweiConnectApi {


    /**
     * 获取token
     */
    @POST("api/oauth2/v1/token")
    suspend fun getToken(
        @Body params: HWTokenParams
    ): HWTokenResp


    /**
     * 通过包名获取AppId
     */
    @GET("api/publish/v2/appid-list")
    suspend fun getAppId(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("packageName") packageName: String,
    ): HWAppIdResp

    /**
     * 获取文件上传地址
     */
    @GET("api/publish/v2/upload-url/for-obs")
    suspend fun getUploadUrl(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Query("fileName") fileName: String,
        @Query("contentLength") contentLength: Long,
    ): HWUploadUrlResp

    /**
     * Apk上传以后，通过这个接口刷新文件
     */
    @PUT("api/publish/v2/app-file-info")
    suspend fun refreshApkFile(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Body params: HWRefreshApk
    ): HWResp

    /**
     * 获取Apk编译状态
     */
    @GET("api/publish/v2/package/compile/status")
    suspend fun getApkCompileState(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Query("pkgIds") pkgIds: String = appId,
    ): HWApkState

    /**
     * 获取Apk编译状态
     */
    @GET("api/publish/v2/aab/complile/status")
    suspend fun getApkCompileState2(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Query("pkgVersion") pkgVersion: String ,
    ): HWApkState
    /**
     * 更新版本描述
     */
    @PUT("api/publish/v2/app-language-info")
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
    @POST("api/publish/v2/app-submit")
    suspend fun submit(
        @Header("client_id") clientId: String,
        @Header("Authorization") token: String,
        @Query("appId") appId: String
    ): HWResp

}

/**
 * 上传文件
 */
suspend fun HuaweiConnectApi.uploadFile(
    file: File,
    url: HWUploadUrlResp.UploadUrl,
    progressChange: ProgressChange
): Unit = withContext(Dispatchers.IO) {
    val client = OkHttpFactory.default()
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