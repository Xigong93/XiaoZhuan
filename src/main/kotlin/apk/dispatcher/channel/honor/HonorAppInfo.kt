package apk.dispatcher.channel.honor

import apk.dispatcher.channel.MarketState
import apk.dispatcher.channel.ReviewState
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HonorAppInfo(
    @Json(name = "languageInfo")
    val languageInfo: List<LanguageInfo>,
    @Json(name = "releaseInfo")
    val releaseInfo: PubReleaseInfo
) {
    @JsonClass(generateAdapter = true)
    data class LanguageInfo(
        @Json(name = "languageId") val languageId: String,
        @Json(name = "appName") val appName: String,
        @Json(name = "intro") val intro: String,
        @Json(name = "briefIntro") val briefIntro: String?
    )

    /**
     * 线上版本信息
     */
    @JsonClass(generateAdapter = true)
    data class PubReleaseInfo(
        @Json(name = "versionCode")
        val versionCode: Long,
        @Json(name = "versionName")
        val versionName: String
    )

}