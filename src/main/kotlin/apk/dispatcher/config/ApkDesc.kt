package apk.dispatcher.config

import androidx.compose.runtime.Stable
import apk.dispatcher.MoshiFactory
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Apk的简单描述信息
 */
@Stable
@JsonClass(generateAdapter = false)
data class ApkDesc(
    @Json(name = "name")
    val name: String,
    @Json(name = "applicationId")
    val applicationId: String,
    /** 创建时间，unix时间戳，毫秒 */
    @Json(name = "createTime")
    val createTime: Long,
) {
    companion object {
        val adapter = MoshiFactory.getAdapter<ApkDesc>()
    }
}
