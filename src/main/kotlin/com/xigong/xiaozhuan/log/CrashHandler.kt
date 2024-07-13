package com.xigong.xiaozhuan.log

import kotlin.time.Duration.Companion.milliseconds

object CrashHandler {

    fun install() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            AppLogger.error("App崩溃", "${t.name} 发生异常", e)
            AppLogger.awaitTermination(200.milliseconds)
            defaultHandler.uncaughtException(t, e)
        }
    }
}