package apk.dispatcher.huawei

import apk.dispatcher.ApiException
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
class SampleResult(
    @Json(name = "ret")
    val result: Result
)

@JsonClass(generateAdapter = false)
data class Result(
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