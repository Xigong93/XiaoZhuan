package apk.dispatcher.channel.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HWRefreshApk(
    @Json(name = "fileType")
    val fileType: Int = 5,
    val files: List<FileInfo>
) {
    @JsonClass(generateAdapter = false)
    data class FileInfo(
        @Json(name = "fileName")
        val fileName: String,
        @Json(name = "fileDestUrl")
        val fileDestUrl: String
    )
}