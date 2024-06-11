package apk.dispatcher.channel.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HWUploadUrlResp(
    @Json(name = "ret")
    val result: HWResult,
    @Json(name = "urlInfo")
    val url: UploadUrl?
) {
    @JsonClass(generateAdapter = true)
    data class UploadUrl(
        @Json(name = "url")
        val url: String,
        @Json(name = "objectId")
        val objectId: String,
        @Json(name = "headers")
        val headers: Map<String, String> = emptyMap()
    )
}