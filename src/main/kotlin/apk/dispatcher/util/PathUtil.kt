package apk.dispatcher.util

import java.io.File
import java.io.FileFilter

object PathUtil {

    fun getApkDispatcherDir(): File {
        val homeDir = File(requireNotNull(System.getProperty("user.home")))
        return File(homeDir, ".apkDispatcher")
    }

    fun listApkFile(dir: File): Array<File> {
        return dir.listFiles(FileFilter {
            it.name.endsWith(".apk", true)
        }) ?: emptyArray()
    }
}