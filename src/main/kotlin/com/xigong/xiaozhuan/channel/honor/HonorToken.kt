package com.xigong.xiaozhuan.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
data class HonorTokenResp(
    @Json(name = "access_token")
    val token: String?
)