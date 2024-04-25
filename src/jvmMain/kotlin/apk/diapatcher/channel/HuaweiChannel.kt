package apk.diapatcher.channel

import apk.diapatcher.ApkChannel
import java.io.File

class HuaweiChannel : ApkChannel() {

    override val channelName: String = "华为"

    override val fileNameIdentify: String = "HUAWEI"

    override val paramDefine: List<Param> = listOf(
        Param("AppId"),
        Param("AppKey"),
    )

    override fun init(params: Map<Param, String>) {

    }

    override fun performUpload(file: File, progress: (Int) -> Unit) {
        TODO("Not yet implemented")
    }


}