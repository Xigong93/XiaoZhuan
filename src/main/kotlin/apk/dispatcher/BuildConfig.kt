package apk.dispatcher

import apk.dispatcher.log.AppLogger
import com.google.gson.Gson
import com.google.gson.JsonObject

object BuildConfig {

    private val config = loadBuildConfig()

    val debug: Boolean = !config.get("release").asBoolean

    val versionCode: Long = config.get("versionCode").asLong

    val versionName: String = config.get("versionName").asString

    fun print() {
        AppLogger.info("BuildConfig", "构建配置:$config")
    }
}

private fun loadBuildConfig(): JsonObject {
    val loader = Thread.currentThread().contextClassLoader
    val stream = loader.getResourceAsStream("META-INF/BuildConfig.json")
    requireNotNull(stream)
    return stream.reader().use { Gson().fromJson(it, JsonObject::class.java) }
}