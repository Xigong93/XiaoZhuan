package apk.dispatcher.log

object CrashHandler {

    fun install() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            AppLogger.error("App崩溃", "${t.name} 发生异常", e)
            defaultHandler.uncaughtException(t, e)
        }
    }
}