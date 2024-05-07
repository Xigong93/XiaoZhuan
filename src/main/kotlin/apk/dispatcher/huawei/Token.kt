package apk.dispatcher.huawei

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class TokenParams(
    @Json(name = "client_id")
    val clientId: String,
    @Json(name = "client_secret")
    val clientSecret: String,
) {
    @Json(name = "grant_type")
    val type: String = "client_credentials"
}

@JsonClass(generateAdapter = false)
data class TokenResult(
    @Json(name = "access_token")
    val token: String?,
    @Json(name = "ret")
    val result: Result
)