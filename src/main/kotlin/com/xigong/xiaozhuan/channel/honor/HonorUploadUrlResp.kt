package com.xigong.xiaozhuan.channel.honor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HonorUploadFile(
    @Json(name = "fileName")
    val fileName:String,
    @Json(name = "fileType")
    val fileType:Int,
    @Json(name = "fileSize")
    val fileSize:Long,
    @Json(name = "fileSha256")
    val fileSha256:String,
)


@JsonClass(generateAdapter = true)
data class HonorUploadUrl(
    @Json(name = "uploadUrl")
    val url: String,
    @Json(name = "objectId")
    val objectId: Long,
)