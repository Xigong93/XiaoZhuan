package com.xigong.xiaozhuan.page.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xigong.xiaozhuan.channel.ChannelRegistry
import com.xigong.xiaozhuan.channel.SubmitState
import com.xigong.xiaozhuan.channel.TaskLauncher
import com.xigong.xiaozhuan.config.ApkConfig
import com.xigong.xiaozhuan.config.ApkConfigDao
import com.xigong.xiaozhuan.log.AppLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class UploadVM(
    private val uploadParam: UploadParam
) : ViewModel() {

    private val configDao = ApkConfigDao()

    val taskLaunchers: List<TaskLauncher> = ChannelRegistry.channels
        .filter { uploadParam.channels.contains(it.channelName) }
        .map { TaskLauncher(it) }

    private var submitJob: Job? = null

    init {
        AppLogger.info(LOG_TAG, "init")
    }


    /**
     * 开始分发
     */
    fun startDispatch() {
        AppLogger.info(LOG_TAG, "开始分发")
        submitJob?.takeIf { it.isActive }?.cancel()
        submitJob = viewModelScope.launch {
            taskLaunchers.executeUpload()
        }
    }

    /**
     * 重试
     */
    fun retryDispatch() {
        AppLogger.info(LOG_TAG, "重试")
        submitJob?.takeIf { it.isActive }?.cancel()
        submitJob = viewModelScope.launch {
            val launchers = taskLaunchers.filter { it.getSubmitState().value is SubmitState.Error }
            launchers.executeUpload()
        }
    }

    private suspend fun List<TaskLauncher>.executeUpload() {
        val file = File(uploadParam.apkFile)
        forEach {
            it.setChannelParam(getApkConfig().channels)
            it.selectFile(file)
            it.prepare()
        }
        val updateDesc = uploadParam.updateDesc.trim()
        forEach { it.startSubmit(updateDesc) }
    }


    private suspend fun getApkConfig(): com.xigong.xiaozhuan.config.ApkConfig {
        return checkNotNull(configDao.getConfig(uploadParam.appId)) { "获取配置失败" }
    }

    /**
     * 取消分发
     */
    fun cancelDispatch() {
        AppLogger.info(LOG_TAG, "取消分发")
        submitJob?.takeIf { it.isActive }?.cancel("用户取消")
    }


    companion object {
        private const val LOG_TAG = "应用市场提交"
    }
}
