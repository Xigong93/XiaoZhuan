package apk.dispatcher.channel.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
class HWBindFileResp(
    @Json(name = "ret")
    val result: HWResult,
    @Json(name = "pkgVersion")
    val pkgVersion: List<String>
) {
    val pkgId: String get() = requireNotNull(pkgVersion.firstOrNull()) { "pkgId为空" }
}