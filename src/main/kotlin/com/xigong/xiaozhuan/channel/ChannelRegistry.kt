package com.xigong.xiaozhuan.channel

import com.xigong.xiaozhuan.BuildConfig
import com.xigong.xiaozhuan.channel.honor.HonorChannelTask
import com.xigong.xiaozhuan.channel.huawei.HuaweiChannelTask
import com.xigong.xiaozhuan.channel.mi.MiChannelTask
import com.xigong.xiaozhuan.channel.oppo.OPPOChannelTask
import com.xigong.xiaozhuan.channel.vivo.VIVOChannelTask

private const val DEBUG_TASK = false

object ChannelRegistry {

    private val realChannels: List<ChannelTask> = listOf(
        HuaweiChannelTask(),
        MiChannelTask(),
        OPPOChannelTask(),
        VIVOChannelTask(),
        HonorChannelTask()
    )

    private val mockChannels: List<ChannelTask> = listOf(
        MockChannelTask("华为", "HUAWEI"),
        MockChannelTask("小米", "MI"),
        MockChannelTask("OPPO", "OPPO"),
        MockChannelTask("VIVO", "VIVO"),
    )

    val channels: List<ChannelTask> = if (DEBUG_TASK && BuildConfig.debug) mockChannels else realChannels


    fun getChannel(name: String): ChannelTask? {
        return channels.firstOrNull { it.channelName == name }
    }
}