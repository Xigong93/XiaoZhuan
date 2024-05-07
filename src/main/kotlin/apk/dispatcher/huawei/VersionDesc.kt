package apk.dispatcher.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class VersionDesc(
    @Json(name = "newFeatures")
    val desc: String
) {
    @Json(name = "lang")
    val language: String = "zh-CN"
}