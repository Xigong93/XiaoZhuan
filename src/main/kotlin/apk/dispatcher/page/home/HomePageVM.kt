package apk.dispatcher.page.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.log.AppLogger

class HomePageVM {

    private val apkConfigDao = ApkConfigDao()


    var apkList: List<ApkConfig> by mutableStateOf(emptyList())


    var currentApk: ApkConfig? by mutableStateOf(null)


    init {
        loadData()
    }

    fun loadData() {
        apkList = apkConfigDao.getConfigList()
        currentApk = apkList.firstOrNull()
    }

    fun deleteCurrent() {
        val apk = currentApk
        if (apk != null) {
            AppLogger.info(LOG_TAG, "删除Apk配置:${apk.applicationId}")
            apkConfigDao.removeConfig(apk)
            loadData()
        }
    }

    companion object {
        private const val LOG_TAG = "首页"
    }

}