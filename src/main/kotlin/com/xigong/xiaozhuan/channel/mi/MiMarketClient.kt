package com.xigong.xiaozhuan.channel.mi

import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.log.action
import com.xigong.xiaozhuan.util.ApkInfo
import java.io.File
import kotlin.math.roundToInt

class MiMarketClient(
    account: String,
    publicKey: String,
    privateKey: String
) {

    private val marketApi = MiMarketApi(account, publicKey, privateKey)

    /**
     * "获取App信息"
     */
    suspend fun getAppInfo(
        applicationId: String
    ): MiAppInfoResp = AppLogger.action(LOG_TAG, "获取App信息") {
        marketApi.getAppInfo(applicationId)
    }

    /**
     * 提交新版本
     */
    suspend fun submit(
        file: File, apkInfo: ApkInfo, updateDesc: String, progress: (Int) -> Unit
    ): Unit = AppLogger.action(LOG_TAG, "提交新版本") {
        val appInfo = getAppInfo(apkInfo.applicationId)
        uploadApk(file, appInfo, updateDesc, progress)
    }

    /**
     * 上传Apk
     */
    private suspend fun uploadApk(
        file: File,
        appInfo: MiAppInfoResp,
        updateDesc: String,
        progress: (Int) -> Unit
    ): Unit = AppLogger.action(LOG_TAG, "上传Apk文件，并提交审核") {
        marketApi.uploadApk(file, appInfo.packageInfo, updateDesc) {
            progress((it * 100).roundToInt())
        }
    }

    companion object {
        private const val LOG_TAG = "小米应用市场Api"
    }
}