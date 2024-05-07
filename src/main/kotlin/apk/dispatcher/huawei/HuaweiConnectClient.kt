package apk.dispatcher.huawei

import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.getApkInfo
import java.io.File

class HuaweiConnectClient {

    private val connectApi = HuaweiConnectApi()

    /**
     * @param file apk文件
     * @param clientId 接口参数
     * @param clientSecret 接口参数
     * @param updateDesc 更新描述
     * @param progressChange 上传进度回调
     */
    @Throws
    suspend fun uploadApk(
        file: File,
        clientId: String,
        clientSecret: String,
        updateDesc: String,
        progressChange: ProgressChange
    ) {
        val apkInfo = getApkInfo(file)
        val rawToken = getToken(clientId, clientSecret)
        val token = "Bearer $rawToken"
        val appId = getAppId(clientId, token, apkInfo.applicationId)
        val uploadUrl = getUploadUrl(clientId, token, appId, file)
        uploadFile(file, uploadUrl, progressChange)
        changeUpdateDesc(clientId, token, appId, updateDesc)
        submit(clientId, token, appId)
    }


    /**
     * 获取token
     */
    private suspend fun getToken(clientId: String, clientSecret: String): String {
        val result = connectApi.getToken(TokenParams(clientId, clientSecret))
        result.result.throwOnFail()
        return checkNotNull(result.token)
    }

    /**
     * 获取AppId
     */
    private suspend fun getAppId(clientId: String, token: String, applicationId: String): String {
        val result = connectApi.getAppId(clientId, token, applicationId)
        result.result.throwOnFail()
        val appIds = result.list ?: emptyList()
        check(appIds.isNotEmpty())
        return appIds.first().id
    }

    /**
     * 获取Apk上传地址
     */
    private suspend fun getUploadUrl(
        clientId: String,
        token: String,
        appId: String,
        file: File,
    ): UploadUrlResult.UploadUrl {
        val result = connectApi.getUploadUrl(clientId, token, appId, file.name, file.length())
        result.result.throwOnFail()
        return checkNotNull(result.url)
    }

    /**
     * 上传文件
     */
    private suspend fun uploadFile(
        file: File,
        url: UploadUrlResult.UploadUrl,
        progressChange: ProgressChange
    ) {
        connectApi.uploadFile(file, url, progressChange)
    }

    /**
     * 修改新版本更新描述
     */
    private suspend fun changeUpdateDesc(
        clientId: String,
        token: String,
        appId: String,
        updateDesc: String
    ) {
        val desc = VersionDesc(updateDesc)
        val result = connectApi.updateVersionDesc(clientId, token, appId, desc)
        result.result.throwOnFail()
    }

    /**
     * 提交审核
     */
    private suspend fun submit(
        clientId: String,
        token: String,
        appId: String,
    ) {
        val result = connectApi.submit(clientId, token, appId)
        result.result.throwOnFail()
    }

}