package apk.dispatcher.channel.huawei

import apk.dispatcher.channel.ApiException
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
class HWResp(
    @Json(name = "ret")
    val result: HWResult
)

@JsonClass(generateAdapter = false)
data class HWResult(
    @Json(name = "code")
    val code: Int,
    @Json(name = "msg")
    val msg: String
) {

    @Suppress("MemberVisibilityCanBePrivate")
    val isSuccess get() = code == 0

    fun throwOnFail() {
        if (!isSuccess) {
            throw ApiException(code, msg)
        }
    }
}