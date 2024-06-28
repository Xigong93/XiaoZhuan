package apk.dispatcher.log

import apk.dispatcher.AppPath
import java.io.*
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

private val DefaultLogger: AppLogger by lazy { AppLoggerImpl() }

interface AppLogger {

    fun debug(tag: String, message: String, throwable: Throwable? = null)

    fun error(tag: String, message: String, throwable: Throwable? = null)

    fun info(tag: String, message: String, throwable: Throwable? = null)

    fun log(level: Level, tag: String, message: String, throwable: Throwable? = null)

    enum class Level {
        Debug,
        Error,
        Info
    }

    companion object : AppLogger by DefaultLogger
}


private class AppLoggerImpl : AppLogger {

    private val fileWriter: Writer

    private val infoConsoleWriter = System.out.writer()

    private val errorConsoleWriter = System.err.writer()

    private val loggerExecutor = Executors.newSingleThreadExecutor { Thread(it).apply { name = "Logger" } }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    init {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val file = File(AppPath.getLogDir(), "${date}.log")
        file.parentFile.mkdirs()
        fileWriter = FileOutputStream(file, true).bufferedWriter()
    }

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        log(AppLogger.Level.Debug, tag, message, throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        log(AppLogger.Level.Error, tag, message, throwable)
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        log(AppLogger.Level.Info, tag, message, throwable)
    }

    override fun log(level: AppLogger.Level, tag: String, message: String, throwable: Throwable?) {
        val threadName = Thread.currentThread().name
        // dateFormatter 线程安全处理，防止时间错乱
        val time: String = synchronized(dateFormatter) { dateFormatter.format(Date()) }

        // 输出到控制台
        if (level == AppLogger.Level.Error) {
            errorConsoleWriter.writeLog(threadName, time, level, tag, message, throwable)
        } else {
            infoConsoleWriter.writeLog(threadName, time, level, tag, message, throwable)
        }

        // 写入文件
        loggerExecutor.execute {
            fileWriter.writeLog(threadName, time, level, tag, message, throwable)
        }

    }

    private fun Writer.writeLog(
        threadName: String,
        time: String,
        level: AppLogger.Level,
        tag: String,
        message: String,
        t: Throwable?
    ) {
        write(time)
        write(" ")
        write("[")
        write(threadName)
        write("]")
        write(" ")
        write(level.name)
        write("/")
        write(tag)
        write(": ")
        write(message)
        write(" ")
        if (t != null) {
            write(getStackTraceString(t))
        }
        write(System.lineSeparator())
        flush()
    }


}

private fun getStackTraceString(tr: Throwable): String {
    // This is to reduce the amount of log spew that apps do in the non-error
    // condition of the network being unavailable.
    var t: Throwable? = tr
    while (t != null) {
        if (t is UnknownHostException) {
            return ""
        }
        t = t.cause
    }

    val sw = StringWriter()
    val pw = PrintWriter(sw)
    tr.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}

