package com.xigong.xiaozhuan.page.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xigong.xiaozhuan.channel.ChannelRegistry
import com.xigong.xiaozhuan.channel.ChannelTask
import com.xigong.xiaozhuan.config.ApkConfig
import com.xigong.xiaozhuan.config.ApkConfigDao
import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.widget.Toast
import kotlinx.coroutines.launch

class ApkConfigVM(
    private val appId: String?,
) : ViewModel() {

    private val configDao = ApkConfigDao()

    private val channels: List<ChannelTask> = ChannelRegistry.channels


    var apkConfigState by mutableStateOf(createApkConfig(null))

    init {
        viewModelScope.launch {
            apkConfigState = createApkConfig(configDao.getConfig(appId ?: ""))
        }
    }

    fun updateChannel(channel: ApkConfig.Channel) {
        apkConfigState = apkConfigState.copy(channels = apkConfigState.channels.map {
            if (it.name == channel.name) channel else it
        })
    }

    /**
     * 保存配置
     */
    suspend fun saveApkConfig(): Boolean {
        val apkConfig = apkConfigState
        val appName = apkConfig.name.trim()
        if (appName.isEmpty()) {
            Toast.show("请输入App名称")
            return false
        }
        val applicationId = apkConfig.applicationId.trim()
        if (applicationId.isEmpty()) {
            Toast.show("请输入ApplicationId")
            return false
        }
        for (channel in apkConfig.channels) {
            if (!channel.enable) continue
            if (channel.params.any { it.value.isEmpty() }) {
                Toast.show("${channel.name}渠道,参数未填充完整")
                return false
            }
        }
        if (apkConfig.channels.all { !it.enable }) {
            Toast.show("请至少启用一个渠道")
            return false
        }
        AppLogger.info(LOG_TAG, "保存配置:${apkConfig.applicationId}")
        AppLogger.debug(LOG_TAG, "保存配置:${apkConfig}")
        try {
            // 先删除原来的，避免修改了包名，导致有两个配置
            configDao.removeConfig(appId ?: "")
            configDao.saveConfig(apkConfig)
            return true
        } catch (e: Exception) {
            AppLogger.error(LOG_TAG, "保存Apk配置失败", e)
            Toast.show("保存失败")
        }
        return false

    }

    /**
     * 用老的配置和渠道配置，生成一个界面的配置对象
     */
    private fun createApkConfig(oldApk: ApkConfig?): ApkConfig {
        val channelConfigs = channels.map { chan ->
            val oldChan = oldApk?.getChannel(chan.channelName)
            createChannelConfig(chan.channelName, oldChan)
        }
        return ApkConfig(
            name = oldApk?.name ?: "",
            applicationId = oldApk?.applicationId ?: "",
            createTime = oldApk?.createTime ?: System.currentTimeMillis(),
            channels = channelConfigs,
            enableChannel = oldApk?.enableChannel ?: false,
            extension = oldApk?.extension ?: ApkConfig.Extension()
        )
    }

    private fun createChannelConfig(name: String, oldChannel: ApkConfig.Channel?): ApkConfig.Channel {
        val params = ChannelRegistry.getChannel(name)?.getParams()?.map {
            val oldValue = oldChannel?.getParam(it.name)?.value
            ApkConfig.Param(it.name, oldValue ?: it.defaultValue ?: "")
        }
        return ApkConfig.Channel(
            name = oldChannel?.name ?: name,
            enable = oldChannel?.enable ?: true,
            params = params ?: emptyList()
        )
    }

    companion object {
        private const val LOG_TAG = "Apk配置"
    }


}