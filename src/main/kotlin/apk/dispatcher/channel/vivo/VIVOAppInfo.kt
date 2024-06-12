package apk.dispatcher.channel.vivo

import com.google.gson.JsonObject

class VIVOAppInfo(obj: JsonObject) {
    /**
     * 应用分类（appClassify
     * https://dev.vivo.com.cn/documentCenter/doc/344
     */
    val onlineType: Int = obj.get("onlineType").asInt
    override fun toString(): String {
        return "VIVOAppInfo(onlineType=$onlineType)"
    }

}