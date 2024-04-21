package apk.diapatcher

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

fun ApkConfigDao(): ApkConfigDao {
    return ApkConfigDaoImpl()
}

interface ApkConfigDao {
    fun getApkConfigList(): List<ApkConfig>

    fun saveApkConfig(apkConfig: ApkConfig)

    fun removeApkConfig(apkConfig: ApkConfig)
}

private class ApkConfigDaoImpl : ApkConfigDao {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val jsonAdapter by lazy { moshi.adapter(ApkConfig::class.java) }

    override fun getApkConfigList(): List<ApkConfig> {
        val files = getApkDir().listFiles() ?: emptyArray()
        return files.mapNotNull(::readApkConfig)
    }

    override fun saveApkConfig(apkConfig: ApkConfig) {
        writeApkConfig(getApkConfigFile(apkConfig), apkConfig)
    }

    override fun removeApkConfig(apkConfig: ApkConfig) {
        getApkConfigFile(apkConfig).delete()
    }

    private fun readApkConfig(file: File): ApkConfig? {
        return try {
            val json = file.readText(charset = Charsets.UTF_8)
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            file.delete()
            null
        }
    }

    private fun writeApkConfig(file: File, apkConfig: ApkConfig) {
        try {
            val json = jsonAdapter.toJson(apkConfig)
            file.parentFile?.mkdirs()
            file.writeText(json, charset = Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getApkDispatcherDir(): File {
        val homeDir = File(requireNotNull(System.getProperty("user.home")))
        return File(homeDir, ".apkDispatcher")
    }

    private fun getApkDir(): File {
        return File(getApkDispatcherDir(), "apk")
    }

    private fun getApkConfigFile(apkConfig: ApkConfig): File {
        return File(getApkDir(), "${apkConfig.name}.json")
    }


}