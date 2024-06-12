package apk.dispatcher.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass



@JsonClass(generateAdapter = false)
data class HonorAppId(
    @Json(name = "packageName")
    val packageName: String,
    @Json(name = "appId")
    val appId: String,
)