package apk.dispatcher.page.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import apk.dispatcher.channel.ChannelRegistry
import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.config.ApkConfigDao
import apk.dispatcher.widget.Toast

class ApkConfigVM(
    apkConfig: ApkConfig?,
    private val channels: List<ChannelTask> = ChannelRegistry.channels

) {
    var apkConfigState by mutableStateOf(createApkConfig(apkConfig))

    fun updateChannel(channel: ApkConfig.Channel) {
        apkConfigState = apkConfigState.copy(channels = apkConfigState.channels.map {
            if (it.name == channel.name) channel else it
        })
    }

    /**
     * 保存配置
     */
    fun saveApkConfig(): Boolean {
        val apkConfig = apkConfigState
        val apkConfigDao = ApkConfigDao()
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

        try {
            apkConfigDao.saveConfig(apkConfig)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            if (appName.isEmpty()) {
                Toast.show("保持失败")
            }
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
}