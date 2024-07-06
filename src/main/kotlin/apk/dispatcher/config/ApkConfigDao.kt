package apk.dispatcher.config

import apk.dispatcher.AppPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private val instance by lazy { ApkConfigDaoImpl() }
fun ApkConfigDao(): ApkConfigDao {
    return instance
}

interface ApkConfigDao {
    suspend fun getApkList(): List<ApkConfig>

    suspend fun getConfig(id: String): ApkConfig?

    @Throws
    suspend fun saveConfig(apkConfig: ApkConfig)

    suspend fun removeConfig(appId: String)

    suspend fun isEmpty(): Boolean
}

private class ApkConfigDaoImpl : ApkConfigDao {

    private val jsonAdapter by lazy { ApkConfig.adapter }

    override suspend fun getApkList(): List<ApkConfig> = withContext(Dispatchers.IO) {
        val files = AppPath.getApkDir().listFiles() ?: emptyArray()
        files.filter { it.name.endsWith(FILE_SUFFIX) }
            .mapNotNull(::readApkConfig)
            .sortedBy { it.createTime }
    }

    override suspend fun getConfig(id: String): ApkConfig? = withContext(Dispatchers.IO) {
        val file = File(AppPath.getApkDir(), "${id}$FILE_SUFFIX")
        if (file.exists()) {
            readApkConfig(file)
        } else {
            null
        }
    }

    @Throws
    override suspend fun saveConfig(apkConfig: ApkConfig) = withContext(Dispatchers.IO) {
        writeApkConfig(apkConfig.file, apkConfig)
    }

    override suspend fun removeConfig(appId: String) = withContext(Dispatchers.IO) {
        val file = File(AppPath.getApkDir(), "${appId}$FILE_SUFFIX")
        if (file.exists()) {
            val bakFile = File(file.absolutePath + ".bak")
            if (bakFile.exists()) bakFile.delete()
            file.renameTo(bakFile)
        }
        Unit
    }

    override suspend fun isEmpty(): Boolean = withContext(Dispatchers.IO) {
        val files = AppPath.getApkDir().listFiles() ?: emptyArray()
        val file = files.firstOrNull { it.name.endsWith(FILE_SUFFIX) }
        file == null || readApkConfig(file) === null
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