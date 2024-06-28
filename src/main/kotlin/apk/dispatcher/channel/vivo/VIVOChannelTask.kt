package apk.dispatcher.channel.vivo

import apk.dispatcher.channel.ChannelTask
import apk.dispatcher.util.defaultLogger
import apk.dispatcher.util.getApkInfo
import java.io.File
import kotlin.math.roundToInt

class VIVOChannelTask : ChannelTask() {

    override val channelName: String = "VIVO"

    override val fileNameIdentify: String = "VIVO"

    override val paramDefine: List<Param> = listOf(ACCESS_KEY, ACCESS_SECRET)

    private var accessKey = ""

    private var accessSecret = ""

    override fun init(params: Map<Param, String?>) {
        accessKey = params[ACCESS_KEY] ?: ""
        accessSecret = params[ACCESS_SECRET] ?: ""
    }

    override suspend fun performUpload(file: File, updateDesc: String, progress: (Int) -> Unit) {
        val api = VIVOMarketApi(accessKey, accessSecret)
        val apkInfo = getApkInfo(file)
        val appDetail = api.getAppInfo(apkInfo.applicationId)
        defaultLogger.info("查看App详情:${appDetail}")
        val apkResult = api.uploadApk(file, apkInfo.applicationId) {
            progress((it * 100).roundToInt())
        }
        defaultLogger.info("上传apk结果:${apkResult}")
        api.submit(apkResult, updateDesc, appDetail)

    }

    companion object {
        private val ACCESS_KEY = Param("access_key")

        private val ACCESS_SECRET = Param("access_secret")
    }


}