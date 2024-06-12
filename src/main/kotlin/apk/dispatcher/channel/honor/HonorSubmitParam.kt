package apk.dispatcher.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HonorSubmitParam(
    /**
     * 全网发布
     */
    @Json(name = "releaseType")
    val releaseType: Int = 1
)