package com.xigong.xiaozhuan.config

import com.xigong.xiaozhuan.MoshiFactory
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Apk 配置
 * 注意事项：
 * 1. 如果新增参数，需要设置默认值，不然老版本的配置会报错
 */
@JsonClass(generateAdapter = false)
data class ApkConfig(
    @Json(name = "name")
    val name: String,
    @Json(name = "applicationId")
    val applicationId: String,
    /** 创建时间，unix时间戳，毫秒 */
    @Json(name = "createTime")
    val createTime: Long,
    @Json(name = "channels")
    val channels: List<com.xigong.xiaozhuan.config.ApkConfig.Channel>,
    /** 是否支持多渠道包 */
    @Json(name = "enableChannel")
    val enableChannel: Boolean = true,
    @Json(name = "extension")
    val extension: com.xigong.xiaozhuan.config.ApkConfig.Extension
) {

    fun getChannel(name: String): com.xigong.xiaozhuan.config.ApkConfig.Channel? {
        return channels.firstOrNull { it.name == name }
    }

    /**
     * 渠道是否启用
     */
    fun channelEnable(name: String): Boolean {
        return getChannel(name)?.enable == true
    }

    /**
     * 渠道
     */
    @JsonClass(generateAdapter = true)
    data class Channel(
        /** 名称 */
        @Json(name = "name")
        val name: String,
        /** 是否启用 */
        @Json(name = "enable")
        val enable: Boolean,
        /** 参数 */
        @Json(name = "params")
        val params: List<com.xigong.xiaozhuan.config.ApkConfig.Param>
    ) {
        fun getParam(name: String): com.xigong.xiaozhuan.config.ApkConfig.Param? {
            return params.firstOrNull { it.name == name }
        }
    }

    @JsonClass(generateAdapter = true)
    data class Param(
        @Json(name = "name")
        val name: String,
        @Json(name = "value")
        val value: String
    )

    @JsonClass(generateAdapter = true)
    data class Extension(
        /** 更新描述 */
        @Json(name = "updateDesc")
        val updateDesc: String? = null,
        /** 上次选择的Apk目录 */
        @Json(name = "apkDir")
        val apkDir: String? = null
    )

    companion object {
        val adapter = MoshiFactory.getAdapter<com.xigong.xiaozhuan.config.ApkConfig>()
    }
}