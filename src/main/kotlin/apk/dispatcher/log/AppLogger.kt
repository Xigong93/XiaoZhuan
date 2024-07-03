package apk.dispatcher.log

import apk.dispatcher.AppPath
import apk.dispatcher.BuildConfig
import java.io.*
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

suspend fun <R> AppLogger.action(tag: String, action: String, black: suspend () -> R): R {
    try {
        info(tag, "$action 开始")
        val result = black()
        info(tag, "$action 成功")
        debug(tag, "$action 结果:$result")
        return result
    } catch (e: Exception) {
        error(tag, "$action 失败", e)
        throw e
    }
}

fun <R> AppLogger.actionSync(tag: String, action: String, black: () -> R): R {
    try {
        info(tag, "$action 开始")
        val result = black()
        info(tag, "$action 成功")
        debug(tag, "$action 结果:$result")
        return result
    } catch (e: Exception) {
        error(tag, "$action 失败", e)
        throw e
    }
}

interface AppLogger {

    fun debug(tag: String, message: String, throwable: Throwable? = null)

    fun error(tag: String, message: String, throwable: Throwable? = null)

    fun info(tag: String, message: String, throwable: Throwable? = null)

    fun log(level: Level, tag: String, message: String, throwable: Throwable? = null)

    fun awaitTermination(timeout: Duration)

    enum class Level { Debug, Info, Error }

    companion object : AppLogger by DefaultLogger
}


private object DefaultLogger : AppLogger {

    private val fileWriter: Writer

    private val infoConsole = System.out.writer()

    private val errorConsole = System.err.writer()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    private val loggerExecutor = Executors.newSingleThreadExecutor { Thread(it).apply { name = "Logger" } }

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
        if (loggerExecutor.isShutdown) return
        val thread = Thread.currentThread().name
        val date = Date()
        loggerExecutor.execute {
            try {
                val time = dateFormatter.format(date)
                // 输出到控制台
                val console = if (level == AppLogger.Level.Error) errorConsole else infoConsole
                console.writeLog(level, thread, time, tag, message, throwable)
                // 写入文件
                if (printable(level)) {
                    fileWriter.writeLog(level, thread, time, tag, message, throwable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun awaitTermination(timeout: Duration) {
        try {
            loggerExecutor.shutdown()
            loggerExecutor.awaitTermination(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Writer.writeLog(
        level: AppLogger.Level,
        threadName: String,
        time: String,
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

    private fun printable(level: AppLogger.Level): Boolean {
        return if (BuildConfig.DEBUG) {
            true
        } else {
            level >= AppLogger.Level.Info
        }
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

