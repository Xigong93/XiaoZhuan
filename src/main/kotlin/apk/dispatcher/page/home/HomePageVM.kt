package apk.dispatcher.page.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.config.ApkDesc
import apk.dispatcher.log.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomePageVM : ViewModel() {

    private val configDao = ApkConfigDao()


    private val currentApk = mutableStateOf<ApkDesc?>(null)


    private val apkList = mutableStateOf<List<ApkDesc>>(emptyList())


    val currentAppFlow = MutableStateFlow<String?>(null)

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
            if (new != old && new != null) {
                currentApk.value = new
                currentAppFlow.tryEmit(new.applicationId)
            }
        }
    }

    fun getCurrentApk(): State<ApkDesc?> = currentApk

    fun getApkList(): State<List<ApkDesc>> = apkList

    fun updateCurrent(apkDesc: ApkDesc) {
        currentApk.value = apkDesc
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
        AppLogger.info(LOG_TAG, "clear")

    }

    companion object {
        private const val LOG_TAG = "首页"
    }


}