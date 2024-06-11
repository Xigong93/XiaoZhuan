package apk.dispatcher.channel.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HWApkState(
    @Json(name = "ret")
    val result: HWResult,
    val pkgStateList: List<PackageState>

) {
    @JsonClass(generateAdapter = false)
    data class PackageState(
        @Json(name = "pkgId")
        val pkgId: String,
        @Json(name = "successStatus")
        val successStatus: Int
    ) {
        fun isSuccess(): Boolean = successStatus == 0
    }
}