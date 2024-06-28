package apk.dispatcher.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import apk.dispatcher.ApkConfig
import apk.dispatcher.ApkConfigDao

class HomePageVM {
    val selectedTab = mutableStateOf(0)

    val apkConfigDao = ApkConfigDao()


    val apkConfigs = mutableStateOf<List<ApkConfig>>(emptyList())


    /**
     * 是否显示菜单
     */
    var showMenu by mutableStateOf(false)


    val currentApk: ApkConfig
        get() {
            val index = selectedTab.value
            return apkConfigs.value[index]
        }

    init {
        reload()
    }

    fun reload() {
        apkConfigs.value = apkConfigDao.getApkConfigList()
        selectedTab.value = selectedTab.value.coerceIn(0, (apkConfigs.value.size - 1).coerceAtLeast(0))
    }

    fun deleteCurrent() {
        apkConfigDao.removeApkConfig(currentApk)
        reload()
    }

}