package apk.dispatcher

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
data class ApkConfig(
    @Json(name = "name")
    val name: String,
    /** 创建时间，unix时间戳，毫秒 */
    @Json(name = "createTime")
    val createTime: Long,
    @Json(name = "channels")
    val channels: List<Channel>,
    @Json(name = "extension")
    val extension: ExtensionConfig
) {

    /**
     * 渠道
     */
    @JsonClass(generateAdapter = true)
    data class Channel(
        /** 名称 */
        @Json(name = "name")
        val name: String,
        /** 参数 */
        @Json(name = "params")
        val params: List<Param>
    )

    @JsonClass(generateAdapter = true)
    data class Param(
        @Json(name = "name")
        val name: String,
        @Json(name = "value")
        val value: String
    )

    @JsonClass(generateAdapter = true)
    data class ExtensionConfig(
        /** 是否支持多渠道包 */
        @Json(name = "enableChannel")
        val enableChannel: Boolean = true,
        /** 是否支持32和64位合并包 */
        @Json(name = "enableCombineAbi")
        val enableCombineAbi: Boolean = true
    )
}