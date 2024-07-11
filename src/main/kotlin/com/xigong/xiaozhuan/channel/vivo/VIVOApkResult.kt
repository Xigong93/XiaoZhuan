package com.xigong.xiaozhuan.channel.vivo

import com.google.gson.JsonObject

class VIVOApkResult(obj: JsonObject) {
    val packageName: String = obj.get("packageName").asString

    /**
     * 流水号
     */
    val serialnumber: String = obj.get("serialnumber").asString
    val versionCode: Long = obj.get("versionCode").asLong
    val versionName: String = obj.get("versionName").asString
    val fileMd5: String = obj.get("fileMd5").asString

    override fun toString(): String {
        return "VIVOApkResult(packageName='$packageName', serialnumber='$serialnumber', versionCode=$versionCode, versionName='$versionName', fileMd5='$fileMd5')"
    }


}