package apk.diapatcher

import apk.diapatcher.channel.*

object ApkChannelRegistry {

    val channels: List<ApkChannel> = listOf(
        HuaweiChannel(),
        MiChannel(),
        OPPOChannel(),
        VIVOChannel(),
        HonourChannel()
    )
}