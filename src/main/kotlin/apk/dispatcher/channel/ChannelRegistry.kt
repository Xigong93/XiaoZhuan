package apk.dispatcher.channel

import apk.dispatcher.BuildConfig
import apk.dispatcher.channel.honor.HonorChannelTask
import apk.dispatcher.channel.huawei.HuaweiChannelTask
import apk.dispatcher.channel.mi.MiChannelTask
import apk.dispatcher.channel.oppo.OPPOChannelTask
import apk.dispatcher.channel.vivo.VIVOChannelTask

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