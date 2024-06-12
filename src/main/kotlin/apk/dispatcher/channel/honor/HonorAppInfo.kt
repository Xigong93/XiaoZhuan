package apk.dispatcher.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HonorAppInfo(
    @Json(name = "languageInfo")
    val languageInfo: List<LanguageInfo>
) {
    @JsonClass(generateAdapter = true)
    data class LanguageInfo(
        @Json(name = "languageId") val languageId: String,
        @Json(name = "appName") val appName: String,
        @Json(name = "intro") val intro: String,
        @Json(name = "briefIntro") val briefIntro: String?
    )
}