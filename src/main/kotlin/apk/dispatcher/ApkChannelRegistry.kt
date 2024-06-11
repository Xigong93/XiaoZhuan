package apk.dispatcher

import apk.dispatcher.channel.*
import apk.dispatcher.channel.huawei.HuaweiChannelTask
import apk.dispatcher.channel.mi.MiChannelTask
import apk.dispatcher.channel.oppo.OPPOChannelTask
import apk.dispatcher.channel.vivo.VIVOChannelTask

object ApkChannelRegistry {

    private val realChannels: List<ApkChannelTask> = listOf(
        HuaweiChannelTask(),
        MiChannelTask(),
        OPPOChannelTask(),
        VIVOChannelTask(),
        HonourChannelTask()
    )

    private val mockChannels: List<ApkChannelTask> = listOf(
        MockChannelTask("华为", "HUAWEI"),
        MockChannelTask("小米", "MI")
    )

    val channels: List<ApkChannelTask> = realChannels


    fun getChannel(name: String): ApkChannelTask? {
        return channels.firstOrNull { it.channelName == name }
    }
}