package apk.dispatcher

import apk.dispatcher.channel.*
import apk.dispatcher.huawei.HuaweiChannelTask

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