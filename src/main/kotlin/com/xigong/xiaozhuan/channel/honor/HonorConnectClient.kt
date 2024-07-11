package com.xigong.xiaozhuan.channel.honor

import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.log.action
import com.xigong.xiaozhuan.util.ApkInfo
import com.xigong.xiaozhuan.util.FileUtil
import com.xigong.xiaozhuan.util.ProgressChange
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
    ): Unit = AppLogger.action(LOG_TAG, "提交新版本") {
        val rawToken = getToken(clientId, clientSecret)
        val token = "Bearer $rawToken"
        val appId = getAppId(token, apkInfo.applicationId)
        val languageInfo = getAppInfo(token, appId).languageInfo.first()
        val uploadUrl = getUploadUrl(token, appId, file)
        uploadFile(file, token, uploadUrl, progressChange)
        bindUploadedApk(token, appId, uploadUrl)
        modifyUpdateDesc(token, appId, updateDesc, languageInfo)
        submit(token, appId)
    }

    /**
     * 获取审核状态
     */
    suspend fun getReviewState(
        clientId: String, clientSecret: String, applicationId: String
    ): HonorReviewState = AppLogger.action(LOG_TAG, "获取审核状态") {
        val rawToken = getToken(clientId, clientSecret)
        val token = "Bearer $rawToken"
        val appId = getAppId(token, applicationId)
        val result = connectApi.getReviewState(token, appId)
        result.throwOnFail("获取审核状态")
        checkNotNull(result.data)
    }


    /**
     * 获取token
     */
    private suspend fun getToken(
        clientId: String, clientSecret: String
    ): String = AppLogger.action(LOG_TAG, "获取token") {
        val result = connectApi.getToken(clientId, clientSecret)
        checkNotNull(result.token)
    }

    /**
     * 获取AppId
     */
    private suspend fun getAppId(
        token: String, applicationId: String
    ): String = AppLogger.action(LOG_TAG, "获取AppId") {
        val result = connectApi.getAppId(token, applicationId)
        result.throwOnFail("获取AppId")
        val appIds = result.data ?: emptyList()
        check(appIds.isNotEmpty())
        appIds.first().appId
    }

    /**
     * 获取APP信息
     */
    private suspend fun getAppInfo(
        token: String, appId: String
    ): HonorAppInfo = AppLogger.action(LOG_TAG, "获取App信息") {
        val result = connectApi.getAppInfo(token, appId)
        result.throwOnFail("获取App信息")
        checkNotNull(result.data)
    }


    /**
     * 获取Apk上传地址
     */
    private suspend fun getUploadUrl(
        token: String,
        appId: String,
        file: File,
    ): HonorUploadUrl = AppLogger.action(LOG_TAG, "获取Apk上传地址") {
        val uploadFile = HonorUploadFile(
            file.name, 100, file.length(), FileUtil.getFileSha256(file)
        )
        val result = connectApi.getUploadUrl(token, appId, listOf(uploadFile))
        result.throwOnFail("获取Apk上传地址")
        checkNotNull(result.data).first()
    }

    /**
     * 上传文件
     */
    private suspend fun uploadFile(
        file: File,
        token: String,
        url: HonorUploadUrl,
        progressChange: ProgressChange
    ): Unit = AppLogger.action(LOG_TAG, "上传Apk文件") {
        connectApi.uploadFile(file, token, url, progressChange)
    }

    /**
     * 绑定已上传的apk文件
     */
    private suspend fun bindUploadedApk(
        token: String,
        appId: String,
        url: HonorUploadUrl,
    ): Unit = AppLogger.action(LOG_TAG, "绑定已上传的apk文件") {
        val fileInfo = HonorBindApkFile(listOf(HonorBindApkFile.Item(url.objectId)))
        val result = connectApi.bindApkFile(token, appId, fileInfo)
        result.throwOnFail("绑定已上传的apk文件")
    }

    /**
     * 修改新版本更新描述
     */
    private suspend fun modifyUpdateDesc(
        token: String,
        appId: String,
        updateDesc: String,
        languageInfo: HonorAppInfo.LanguageInfo
    ): Unit = AppLogger.action(LOG_TAG, "修改新版本更新描述") {
        val newInfo = HonorVersionDesc.LanguageInfo(
            appName = languageInfo.appName,
            intro = languageInfo.intro,
            desc = updateDesc,
            briefIntro = languageInfo.briefIntro
        )
        val desc = HonorVersionDesc(listOf(newInfo))
        val result = connectApi.updateVersionDesc(token, appId, desc)
        result.throwOnFail("修改新版本更新描述")
    }

    /**
     * 提交审核
     */
    private suspend fun submit(
        token: String,
        appId: String,
    ): Unit = AppLogger.action(LOG_TAG, "提交审核") {
        val result = connectApi.submit(token, appId)
        result.throwOnFail("提交审核")
    }

    companion object {
        private const val LOG_TAG = "荣耀应用市场Api"
    }
}