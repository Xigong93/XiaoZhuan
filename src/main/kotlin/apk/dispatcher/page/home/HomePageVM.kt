package apk.dispatcher.page.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.log.AppLogger
import kotlinx.coroutines.launch

class HomePageVM : ViewModel() {

    private var apkPageState: ApkPageState? = null

    private val configDao = ApkConfigDao()

    private val currentApk = mutableStateOf<ApkConfig?>(null)

    private val apkList = mutableStateOf<List<ApkConfig>>(emptyList())

    init {
        AppLogger.info(LOG_TAG, "init")
        loadData()
    }


    fun loadData() {
        viewModelScope.launch {
            val configList = configDao.getApkList()
            apkList.value = configList
            val old = currentApk.value
            // 当前Apk为空时，或已被删除时，重新指定
            val new = configList.find { it == old } ?: configList.firstOrNull()
            if (new != null) {
                updateCurrent(new)
            }
        }
    }

    fun getApkVM(): ApkPageState? = apkPageState

    fun getCurrentApk(): State<ApkConfig?> = currentApk

    fun getApkList(): State<List<ApkConfig>> = apkList

    fun updateCurrent(apkDesc: ApkConfig) {
        val old = currentApk.value
        if (old != apkDesc) {
            currentApk.value = apkDesc
            apkPageState?.clear()
            apkPageState = ApkPageState(apkDesc)
        }
    }




    fun deleteCurrent(finish: suspend () -> Unit) {
        viewModelScope.launch {
            val apk = currentApk.value
            if (apk != null) {
                AppLogger.info(LOG_TAG, "删除Apk配置:${apk.applicationId}")
                configDao.removeConfig(apk.applicationId)
                loadData()
            }
            finish()
        }
    }

    override fun onCleared() {
        super.onCleared()
        apkPageState?.clear()
        AppLogger.info(LOG_TAG, "clear")

    }

    companion object {
        private const val LOG_TAG = "首页"
    }


}