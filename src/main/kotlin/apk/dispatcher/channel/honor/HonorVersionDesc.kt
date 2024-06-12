package apk.dispatcher.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HonorVersionDesc(
    @Json(name = "languageInfoList") val list: List<LanguageInfo>
) {
    data class LanguageInfo(
        @Json(name = "appName") val appName: String,
        @Json(name = "intro") val intro: String,
        @Json(name = "briefIntro") val briefIntro: String?,
        @Json(name = "newFeature") val desc: String,
        @Json(name = "languageId") val languageId: String = "zh-CN"
    )
}