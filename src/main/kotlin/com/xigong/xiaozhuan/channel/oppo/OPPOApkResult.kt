package com.xigong.xiaozhuan.channel.oppo

import com.google.gson.JsonObject

class OPPOApkResult(obj: JsonObject) {
    val url: String = obj.get("url").asString
    val md5: String = obj.get("md5").asString
}