package apk.dispatcher.channel.honor

import apk.dispatcher.OkHttpFactory
import apk.dispatcher.RetrofitFactory
import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.ProgressRequestBody
import apk.dispatcher.util.getJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import retrofit2.http.*
import java.io.File

fun HonorConnectApi(): HonorConnectApi {
    return RetrofitFactory.create("https://appmarket-openapi-drcn.cloud.honor.com/")
}

/**
 * 华为提供的Api
 * https://developer.huawei.com/consumer/cn/doc/AppGallery-connect-Guides/agcapi-getstarted-0000001111845114
 */
interface HonorConnectApi {


    /**
     * 获取token
     */
    @POST("https://iam.developer.honor.com/auth/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id")
        clientId: String,
        @Field("client_secret")
        clientSecret: String,
        @Field("grant_type")
        type: String = "client_credentials"
    ): HonorTokenResp


    /**
     * 通过包名获取AppId
     */
    @GET("openapi/v1/publish/get-app-id")
    suspend fun getAppId(
        @Header("Authorization") token: String,
        @Query("pkgName") packageName: String,
    ): HonorResult<List<HonorAppId>>

    /**
     * 获取App信息
     */
    @GET("openapi/v1/publish/get-app-detail")
    suspend fun getAppInfo(
        @Header("Authorization") token: String,
        @Query("appId") appId: String
    ): HonorResult<HonorAppInfo>

    /**
     * 获取文件上传地址
     */
    @POST("openapi/v1/publish/get-file-upload-url")
    suspend fun getUploadUrl(
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Body body: List<HonorUploadFile>
    ): HonorResult<List<HonorUploadUrl>>

    /**
     * Apk上传以后，通过这个接口刷新文件
     */
    @POST("openapi/v1/publish/update-file-info")
    suspend fun bindApkFile(
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Body body: HonorBindApkFile
    ): HonorResult<Any?>


    /**
     * 更新版本描述
     */
    @POST("openapi/v1/publish/update-language-info")
    suspend fun updateVersionDesc(
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Body
        versionDesc: HonorVersionDesc
    ): HonorResult<Any?>


    /**
     * 提交审核
     */
    @POST("openapi/v1/publish/submit-audit")
    suspend fun submit(
        @Header("Authorization") token: String,
        @Query("appId") appId: String,
        @Body param: HonorSubmitParam = HonorSubmitParam()
    ): HonorResult<Any?>

}

/**
 * 上传文件
 */
suspend fun HonorConnectApi.uploadFile(
    file: File,
    token: String,
    url: HonorUploadUrl,
    progressChange: ProgressChange
): Unit = withContext(Dispatchers.IO) {
    val client = OkHttpFactory.default()
    val headers = Headers.Builder()
        .add("Authorization", token)
        .build()
    val contentType = "application/vnd.android.package-archive".toMediaType()
    val apkBody = ProgressRequestBody(
        contentType, file, progressChange
    )
    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)// 强制指定类型，默认是mixed
        .addFormDataPart("file", file.name, apkBody)
        .build()
    val request = Request.Builder()
        .url(url.url)
        .headers(headers)
        .post(body)
        .build()
    val result = client.getJsonResult(request)
    val code = result.get("code").asInt
    check(code == 0) { "上传文件失败,${result}" }
}