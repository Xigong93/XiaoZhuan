package apk.dispatcher.channel

import apk.dispatcher.channel.honor.HonorChannelTask
import apk.dispatcher.channel.huawei.HuaweiChannelTask
import apk.dispatcher.channel.mi.MiChannelTask
import apk.dispatcher.channel.oppo.OPPOChannelTask
import apk.dispatcher.channel.vivo.VIVOChannelTask

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

    val channels: List<ChannelTask> = mockChannels


    fun getChannel(name: String): ChannelTask? {
        return channels.firstOrNull { it.channelName == name }
    }
}