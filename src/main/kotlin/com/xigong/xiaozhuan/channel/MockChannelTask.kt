package com.xigong.xiaozhuan.channel

import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.util.ApkInfo
import kotlinx.coroutines.delay
import java.io.File

class MockChannelTask(
    override val channelName: String,
    override val fileNameIdentify: String
) : ChannelTask() {

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String?>) {

    }

    override suspend fun performUpload(
        file: File,
        apkInfo: ApkInfo,
        versionParams: VersionParams,
        progress: (Int) -> Unit
    ) {
        AppLogger.info(LOG_TAG, "Mock ${channelName},开始上传")
        repeat(100) {
            delay(30)
            progress(it)
        }
        throw ApiException(400, "获取token", "请检测api key")
        AppLogger.info(LOG_TAG, "Mock ${channelName},上传完成")
    }

    override suspend fun getMarketState(applicationId: String): MarketInfo {
        delay(1000)
        throw ApiException(400, "获取token", "请检测api key")
        return MarketInfo(ReviewState.Online, lastVersionCode = 100, lastVersionName = "1.1.0")
    }

    companion object {
        private const val LOG_TAG = "模拟上传"
    }
}