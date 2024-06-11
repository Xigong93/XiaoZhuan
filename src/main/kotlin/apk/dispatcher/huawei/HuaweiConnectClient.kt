package apk.dispatcher.huawei

import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.getApkInfo
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
        refreshApk(clientId, token, appId, file, uploadUrl)
        waitApkReady(clientId, token, appId, apkInfo.versionName)
        modifyUpdateDesc(clientId, token, appId, updateDesc)
        submit(clientId, token, appId)
    }


    /**
     * 获取token
     */
    private suspend fun getToken(clientId: String, clientSecret: String): String {
        val result = connectApi.getToken(HWTokenParams(clientId, clientSecret))
        result.result?.throwOnFail()
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
    ): HWUploadUrlResp.UploadUrl {
        val result = connectApi.getUploadUrl(clientId, token, appId, file.name, file.length())
        result.result.throwOnFail()
        return checkNotNull(result.url)
    }

    /**
     * 上传文件
     */
    private suspend fun uploadFile(
        file: File,
        url: HWUploadUrlResp.UploadUrl,
        progressChange: ProgressChange
    ) {
        connectApi.uploadFile(file, url, progressChange)
    }

    /**
     * 刷新Apk文件
     */
    private suspend fun refreshApk(
        clientId: String,
        token: String,
        appId: String,
        file: File,
        url: HWUploadUrlResp.UploadUrl,
    ) {
        val fileInfo = HWRefreshApk.FileInfo(file.name, url.objectId)
        val params = HWRefreshApk(files = listOf(fileInfo))
        val result = connectApi.refreshApkFile(clientId, token, appId, params)
        result.result.throwOnFail()
    }

    /**
     * 等待Apk编译完成
     */
    private suspend fun waitApkReady(
        clientId: String,
        token: String,
        appId: String,
        versionNumber: String
    ) {
//        val startTime = System.currentTimeMillis()
//        while (true) {
//            if (System.currentTimeMillis() - startTime >= TimeUnit.MINUTES.toMillis(3)) {
//                throw TimeoutException("检测apk状态超时")
//            }
//            val result = connectApi.getApkCompileState(clientId, token, appId)
//            result.result.throwOnFail()
//            if (result.pkgStateList.first().isSuccess()) {
//                break
//            }
//            delay(1.seconds)
//        }
        // 官方文档要求等待2分钟后再提交
        delay(2.minutes)
    }

    /**
     * 修改新版本更新描述
     */
    private suspend fun modifyUpdateDesc(
        clientId: String,
        token: String,
        appId: String,
        updateDesc: String
    ) {
        val desc = HWVersionDesc(updateDesc)
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