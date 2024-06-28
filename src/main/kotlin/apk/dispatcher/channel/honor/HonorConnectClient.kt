package apk.dispatcher.channel.honor

import apk.dispatcher.util.ApkInfo
import apk.dispatcher.util.FileUtil
import apk.dispatcher.util.ProgressChange
import java.io.File

class HonorConnectClient {

    private val connectApi = HonorConnectApi()

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
        apkInfo: ApkInfo,
        clientId: String,
        clientSecret: String,
        updateDesc: String,
        progressChange: ProgressChange
    ) {
        val rawToken = getToken(clientId, clientSecret)
        val token = "Bearer $rawToken"
        val appId = getAppId(token, apkInfo.applicationId)
        val languageInfo = getAppInfo(token, appId)
        val uploadUrl = getUploadUrl(token, appId, file)
        uploadFile(file, token, uploadUrl, progressChange)
        bindUploadedApk(token, appId, uploadUrl)
        modifyUpdateDesc(token, appId, updateDesc, languageInfo)
        submit(token, appId)
    }


    /**
     * 获取token
     */
    private suspend fun getToken(clientId: String, clientSecret: String): String {
        val result = connectApi.getToken(clientId, clientSecret)
        return checkNotNull(result.token)
    }

    /**
     * 获取AppId
     */
    private suspend fun getAppId(token: String, applicationId: String): String {
        val result = connectApi.getAppId(token, applicationId)
        result.throwOnFail()
        val appIds = result.data ?: emptyList()
        check(appIds.isNotEmpty())
        return appIds.first().appId
    }

    private suspend fun getAppInfo(token: String, appId: String): HonorAppInfo.LanguageInfo {
        val result = connectApi.getAppInfo(token, appId)
        result.throwOnFail()
        return checkNotNull(result.data).languageInfo.first()
    }

    /**
     * 获取Apk上传地址
     */
    private suspend fun getUploadUrl(
        token: String,
        appId: String,
        file: File,
    ): HonorUploadUrl {
        val uploadFile = HonorUploadFile(
            file.name, 100, file.length(), FileUtil.getFileSha256(file)
        )
        val result = connectApi.getUploadUrl(token, appId, listOf(uploadFile))
        result.throwOnFail()
        return checkNotNull(result.data).first()
    }

    /**
     * 上传文件
     */
    private suspend fun uploadFile(
        file: File,
        token: String,
        url: HonorUploadUrl,
        progressChange: ProgressChange
    ) {
        connectApi.uploadFile(file, token, url, progressChange)
    }

    /**
     * 绑定已上传的apk文件
     */
    private suspend fun bindUploadedApk(
        token: String,
        appId: String,
        url: HonorUploadUrl,
    ) {
        val fileInfo = HonorBindApkFile(listOf(HonorBindApkFile.Item(url.objectId)))
        val result = connectApi.bindApkFile(token, appId, fileInfo)
        result.throwOnFail()
    }

    /**
     * 修改新版本更新描述
     */
    private suspend fun modifyUpdateDesc(
        token: String,
        appId: String,
        updateDesc: String,
        languageInfo: HonorAppInfo.LanguageInfo
    ) {
        val desc = HonorVersionDesc(
            listOf(
                HonorVersionDesc.LanguageInfo(
                    appName = languageInfo.appName,
                    intro = languageInfo.intro,
                    desc = updateDesc,
                    briefIntro = languageInfo.briefIntro
                )
            )
        )
        val result = connectApi.updateVersionDesc(token, appId, desc)
        result.throwOnFail()
    }

    /**
     * 提交审核
     */
    private suspend fun submit(
        token: String,
        appId: String,
    ) {
        val result = connectApi.submit(token, appId)
        result.throwOnFail()
    }

}