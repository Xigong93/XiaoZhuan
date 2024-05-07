package apk.dispatcher.util

import apk.dispatcher.android.ApkParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class ApkInfo(
    val path: String,
    /**
     * 包名
     */
    val applicationId: String,
    /**
     * 版本号
     */
    val versionCode: Long,
    /**
     * 版本名称
     */
    val versionName: String
)

/**
 * 获取Apk文件信息
 */
@kotlin.jvm.Throws
suspend fun getApkInfo(file: File): ApkInfo {
    return withContext(Dispatchers.IO) { ApkParser().parse(file) }
}