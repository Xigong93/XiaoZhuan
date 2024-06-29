package apk.dispatcher.page.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import apk.dispatcher.config.ApkConfigDao

class HomePageVM {

    private val apkConfigDao = ApkConfigDao()


    var apkList by mutableStateOf(apkConfigDao.getConfigList())


    /**
     * 是否显示菜单
     */
    var showMenu by mutableStateOf(false)


    var currentApk by mutableStateOf(apkList.first())


    fun reload() {
        apkList = apkConfigDao.getConfigList()
        currentApk = apkList.first()
    }

    fun deleteCurrent() {
        apkConfigDao.removeConfig(currentApk)
        reload()
    }

}