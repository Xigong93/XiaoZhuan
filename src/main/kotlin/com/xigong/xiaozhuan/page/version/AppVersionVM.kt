package com.xigong.xiaozhuan.page.version

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xigong.xiaozhuan.BuildConfig
import com.xigong.xiaozhuan.log.AppLogger
import kotlinx.coroutines.launch

class AppVersionVM : ViewModel() {

    var versionState = mutableStateOf<GetVersionState?>(null)

    init {
        getLastVersion()
    }

    fun getLastVersion() {
        viewModelScope.launch {
            versionState.value = try {
                val remoteVersion = VersionRepo.getLastVersion()
                if (remoteVersion.versionCode > BuildConfig.versionCode) {
                    AppLogger.info(LOG_TAG, "发现新版本:${remoteVersion}")
                    GetVersionState.New(remoteVersion)
                } else {
                    AppLogger.info(LOG_TAG, "无新版本")
                    GetVersionState.NoNew
                }
            } catch (e: Exception) {
                AppLogger.error(LOG_TAG, "失败", e)
                GetVersionState.Error
            }
        }
    }

    companion object {
        private const val LOG_TAG = "检测版本更新"
    }
}

sealed class GetVersionState {
    data object Error : GetVersionState()
    data object NoNew : GetVersionState()
    data class New(val version: AppVersion) : GetVersionState()
}