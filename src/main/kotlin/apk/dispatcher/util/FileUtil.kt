package apk.dispatcher.util

import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.io.FileInputStream

object FileUtil {
    /**
     * 获取文件md5
     */
    fun getFileMD5(file: File): String {
        return FileInputStream(file).use { DigestUtils.md5Hex(it) }
    }

    /**
     * 获取文件Sha256
     */
    fun getFileSha256(file: File): String {
        return FileInputStream(file).use { DigestUtils.sha256Hex(it) }
    }

    /**
     * 获取文件尺寸
     */
    fun getFileSize(file: File): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGrouping = 2
        val si = 1000.0
        var bytes = file.length().toDouble()
        var unitIndex = 0
        while (bytes >= si && unitIndex < units.size - 1) {
            bytes /= si
            unitIndex++
        }
        return String.format("%.${digitGrouping}f %s", bytes, units[unitIndex])
    }


}