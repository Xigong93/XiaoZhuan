package apk.dispatcher.util

import java.io.File
import java.nio.file.Files

object PathUtil {

    fun getApkDispatcherDir(): File {
        val homeDir = File(requireNotNull(System.getProperty("user.home")))
        return File(homeDir, ".apkDispatcher")
    }

    fun listApkFile(dir: File): List<File> {
        return dir.walkBottomUp()
            .maxDepth(3)
            .toList()
            .filter { it.name.endsWith(".apk", true) }
            .sortedByDescending { Files.getLastModifiedTime(it.toPath()) }
            .take(9)
    }
}