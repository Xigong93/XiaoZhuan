package apk.dispatcher.page.version

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.jvm.Throws

@JsonClass(generateAdapter = false)
data class GithubRelease(
    @Json(name = "tag_name") val tagName: String,
    @Json(name = "name") val name: String,
    /**
     * 这个可能是富文本
     */
    @Json(name = "body") val body: String,
    /**
     * 网页地址
     */
    @Json(name = "html_url") val htmlUrl: String,
) {
    @Throws
    fun toAppVersion(): AppVersion {
        return AppVersion.from(tagName,name)
    }
}
