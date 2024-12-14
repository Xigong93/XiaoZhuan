package com.xigong.xiaozhuan.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HonorSubmitParam(

    /**
     * 1-全网发布 2-指定时间发布
     */
    @Json(name = "releaseType")
    val releaseType: Int,

    /**
     * 发布类型为指定时间发布时必填
     * 反之非必填
     * UTC时间：yyyy-MM-dd'T'HH:mm:ssZZ，例如“2024-01-01T01:01:01+0800”
     */
    @Json(name = "releaseTime")
    val releaseTime: String?,
)