package apk.dispatcher.config

import apk.dispatcher.AppPath
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

private val instance by lazy { ApkConfigDaoImpl() }
fun ApkConfigDao(): ApkConfigDao {
    return instance
}

interface ApkConfigDao {
    fun getConfigList(): List<ApkConfig>

    fun getConfig(id: String): ApkConfig?

    @Throws
    fun saveConfig(apkConfig: ApkConfig)

    fun removeConfig(apkConfig: ApkConfig)
}

private class ApkConfigDaoImpl : ApkConfigDao {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val jsonAdapter by lazy { moshi.adapter(ApkConfig::class.java) }

    override fun getConfigList(): List<ApkConfig> {
        val files = AppPath.getApkDir().listFiles() ?: emptyArray()
        return files
            .filter { it.name.endsWith(FILE_SUFFIX) }
            .mapNotNull(::readApkConfig)
            .sortedBy { it.createTime }
    }

    override fun getConfig(id: String): ApkConfig? {
        val file = File(AppPath.getApkDir(), "${id}$FILE_SUFFIX")
        return if (file.exists()) {
            readApkConfig(file)
        } else {
            null
        }
    }

    @Throws
    override fun saveConfig(apkConfig: ApkConfig) {
        writeApkConfig(apkConfig.file, apkConfig)
    }

    override fun removeConfig(apkConfig: ApkConfig) {
        val bakFile = File(apkConfig.file.absolutePath + ".bak")
        if (bakFile.exists()) bakFile.delete()
        apkConfig.file.renameTo(bakFile)
    }

    private fun readApkConfig(file: File): ApkConfig? {
        return try {
            val json = file.readText(charset = Charsets.UTF_8)
            jsonAdapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun writeApkConfig(file: File, apkConfig: ApkConfig) {
        val json = jsonAdapter.toJson(apkConfig)
        file.parentFile?.mkdirs()
        file.writeText(json, charset = Charsets.UTF_8)
    }


    private val ApkConfig.file: File
        get() = File(AppPath.getApkDir(), "${applicationId}$FILE_SUFFIX")

    companion object {
        private const val FILE_SUFFIX = ".json"
    }


}