package apk.dispatcher.channel.mi

import apk.dispatcher.MoshiFactory
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
class MiAppInfoResp(
    @Json(name = "packageInfo")
    val packageInfo: MiAppInfo
) {
    @JsonClass(generateAdapter = false)
    class MiAppInfo(
        @Json(name = "appName")
        val appName: String,
        @Json(name = "versionName")
        val versionName: String,
        @Json(name = "versionCode")
        val versionCode: String,
        @Json(name = "packageName")
        val packageName: String,
    )

    companion object {
        val adapter: JsonAdapter<MiAppInfoResp> = MoshiFactory.getAdapter()
    }
}
