package apk.dispatcher.channel.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HWVersionDesc(
    @Json(name = "newFeatures")
    val desc: String,
    @Json(name = "lang")
    val language: String = "zh-CN"
)