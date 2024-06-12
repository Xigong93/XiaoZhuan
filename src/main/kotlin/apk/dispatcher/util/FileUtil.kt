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
     * 获取文件md5
     */
    fun getFileSha256(file: File): String {
        return FileInputStream(file).use { DigestUtils.sha256Hex(it) }
    }
}