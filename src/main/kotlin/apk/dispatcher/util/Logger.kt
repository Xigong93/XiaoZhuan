package apk.dispatcher.util

import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter


val defaultLogger: Logger = Logger.getGlobal().apply {
    val logDir = File(PathUtil.getApkDispatcherDir(), "log")
    logDir.mkdirs()
    val day = SimpleDateFormat("yyyy-MM-dd").format(Date())
    val fileHandler = FileHandler("${logDir.absolutePath}/${day}.log", false)
    fileHandler.formatter = SimpleFormatter()
    addHandler(fileHandler)
}