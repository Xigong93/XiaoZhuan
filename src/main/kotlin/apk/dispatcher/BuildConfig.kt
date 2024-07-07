package apk.dispatcher

import androidx.compose.ui.res.useResource
import apk.dispatcher.log.AppLogger
import com.google.gson.Gson
import com.google.gson.JsonObject

object BuildConfig {

    private val config = loadBuildConfig()

    val debug: Boolean = !config.get("release").asBoolean

    val versionCode: Long = config.get("versionCode").asLong

    val versionName: String = config.get("versionName").asString

    /**
     * 包名
     */
    val packageId: String = config.get("packageId").asString

    /**
     * App名称
     */
    val appName: String = config.get("appName").asString

    /**
     * 启动图标
     */
    const val ICON = "icon.png"


    fun print() {
        AppLogger.info("BuildConfig", "构建配置:$config")
    }
}

private fun loadBuildConfig(): JsonObject {
    return useResource("BuildConfig.json") {
        Gson().fromJson(it.reader(), JsonObject::class.java)
    }
}