package apk.dispatcher.channel.honor

import apk.dispatcher.ApiException
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
data class HonorResult<T>(
    @Json(name = "code")
    val code: Int,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "data")
    val data: T?
) {

    @Suppress("MemberVisibilityCanBePrivate")
    val isSuccess get() = code == 0

    fun throwOnFail() {
        if (!isSuccess) throw ApiException(code, msg)
    }
}