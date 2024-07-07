package apk.dispatcher.channel.huawei

import apk.dispatcher.log.AppLogger
import apk.dispatcher.log.action
import apk.dispatcher.util.ApkInfo
import apk.dispatcher.util.ProgressChange
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
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
        apkInfo: ApkInfo,
        clientId: String,
        clientSecret: String,
        updateDesc: String,
        progressChange: ProgressChange
    ): Unit = AppLogger.action(LOG_TAG, "提交新版本") {
        val rawToken = getToken(clientId, clientSecret)
        val token = "Bearer $rawToken"
        val appId = getAppId(clientId, token, apkInfo.applicationId)
        val uploadUrl = getUploadUrl(clientId, token, appId, file)
        uploadFile(file, uploadUrl, progressChange)
        val bindResult = bindApk(clientId, token, appId, file, uploadUrl)
        waitApkReady(clientId, token, appId, bindResult)
        modifyUpdateDesc(clientId, token, appId, updateDesc)
        submit(clientId, token, appId)
    }

    /**
     * 获取App信息
     */
    @Throws
    suspend fun getAppInfo(
        clientId: String, clientSecret: String, applicationId: String
    ): HWAppInfoResp.AppInfo = AppLogger.action(LOG_TAG, "获取App信息") {
        val rawToken = getToken(clientId, clientSecret)
        val token = "Bearer $rawToken"
        val appId = getAppId(clientId, token, applicationId)
        val appInfo = connectApi.getAppInfo(clientId, token, appId)
        appInfo.result.throwOnFail("获取App信息")
        appInfo.appInfo
    }


    /**
     * 获取token
     */
    private suspend fun getToken(
        clientId: String, clientSecret: String
    ): String = AppLogger.action(LOG_TAG, "获取token") {
        val result = connectApi.getToken(HWTokenParams(clientId, clientSecret))
        result.result?.throwOnFail("获取token")
        checkNotNull(result.token)
    }

    /**
     * 获取AppId
     */
    private suspend fun getAppId(
        clientId: String, token: String, applicationId: String
    ): String = AppLogger.action(LOG_TAG, "获取AppId") {
        val result = connectApi.getAppId(clientId, token, applicationId)
        result.result.throwOnFail("获取AppId")
        val appIds = result.list ?: emptyList()
        check(appIds.isNotEmpty())
        appIds.first().id
    }

    /**
     * 获取Apk上传地址
     */
    private suspend fun getUploadUrl(
        clientId: String,
        token: String,
        appId: String,
        file: File,
    ): HWUploadUrlResp.UploadUrl = AppLogger.action(LOG_TAG, "获取Apk上传地址") {
        val result = connectApi.getUploadUrl(clientId, token, appId, file.name, file.length())
        result.result.throwOnFail("获取Apk上传地址")
        checkNotNull(result.url)
    }

    /**
     * 上传文件
     */
    private suspend fun uploadFile(
        file: File,
        url: HWUploadUrlResp.UploadUrl,
        progressChange: ProgressChange
    ): Unit = AppLogger.action(LOG_TAG, "上传Apk文件") {
        connectApi.uploadFile(file, url, progressChange)
    }

    /**
     * 刷新Apk文件
     */
    private suspend fun bindApk(
        clientId: String,
        token: String,
        appId: String,
        file: File,
        url: HWUploadUrlResp.UploadUrl,
    ): HWBindFileResp = AppLogger.action(LOG_TAG, "绑定Apk文件") {
        val fileInfo = HWRefreshApk.FileInfo(file.name, url.objectId)
        val params = HWRefreshApk(files = listOf(fileInfo))
        val result = connectApi.bindApkFile(clientId, token, appId, params)
        result.result.throwOnFail("绑定Apk文件")
        check(result.pkgVersion.isNotEmpty())
        result
    }

    /**
     * 等待Apk编译完成
     */
    private suspend fun waitApkReady(
        clientId: String,
        token: String,
        appId: String,
        file: HWBindFileResp,
    ): Unit = AppLogger.action(LOG_TAG, "等待Apk编译完成") {
        val startTime = System.currentTimeMillis()
        val timeout = TimeUnit.MINUTES.toMillis(3)
        while (true) {
            delay(10.seconds)
            if (System.currentTimeMillis() - startTime >= timeout) {
                throw TimeoutException("检测apk状态超时")
            }
            val result = connectApi.getApkCompileState(clientId, token, appId, file.pkgId)
            result.result.throwOnFail("检测Apk编译状态")
            if (result.pkgStateList.first().isSuccess()) {
                break
            }
        }
    }

    /**
     * 修改新版本更新描述
     */
    private suspend fun modifyUpdateDesc(
        clientId: String,
        token: String,
        appId: String,
        updateDesc: String
    ): Unit = AppLogger.action(LOG_TAG, "修改新版本更新描述") {
        val desc = HWVersionDesc(updateDesc)
        val result = connectApi.updateVersionDesc(clientId, token, appId, desc)
        result.result.throwOnFail("修改新版本更新描述")
    }

    /**
     * 提交审核
     */
    private suspend fun submit(
        clientId: String,
        token: String,
        appId: String,
    ): Unit = AppLogger.action(LOG_TAG, "提交审核") {
        val result = connectApi.submit(clientId, token, appId)
        result.result.throwOnFail("提交审核")
    }

    companion object {
        private const val LOG_TAG = "华为应用市场Api"
    }

}