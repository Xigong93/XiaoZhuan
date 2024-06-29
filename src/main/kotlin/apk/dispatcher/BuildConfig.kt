package apk.dispatcher

import apk.dispatcher.log.AppLogger

object BuildConfig {
    val TYPE = System.getProperty("buildType", "")

    val DEBUG = TYPE.equals("debug", true)

    fun print() {
        AppLogger.info("BuildConfig", "构建类型:$TYPE")
    }
}