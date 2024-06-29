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
//        MockChannelTask("小米1", "MI1"),
//        MockChannelTask("小米2", "MI2"),
//        MockChannelTask("小米3", "MI3"),
//        MockChannelTask("小米4", "MI4"),
//        MockChannelTask("小米5", "MI5"),
//        MockChannelTask("小米6", "MI6"),
//        MockChannelTask("小米7", "MI7"),
    )

    val channels: List<ChannelTask> = realChannels


    fun getChannel(name: String): ChannelTask? {
        return channels.firstOrNull { it.channelName == name }
    }
}