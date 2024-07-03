package apk.dispatcher.util

import apk.dispatcher.android.ApkParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

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
suspend fun getApkInfo(
    file: File
): ApkInfo = withContext(Dispatchers.IO) {
    try {
        require(file.exists())
        ApkParser.parse(file)
    } catch (e: Exception) {
        throw IOException("解析Apk文件失败,${file.absolutePath}", e)
    }
}
