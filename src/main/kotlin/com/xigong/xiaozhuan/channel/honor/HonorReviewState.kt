package com.xigong.xiaozhuan.channel.honor

import com.xigong.xiaozhuan.channel.MarketInfo
import com.xigong.xiaozhuan.channel.ReviewState
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HonorReviewState(
    /**
     *
     * 0-审核中
     *
     * 1-审核通过
     *
     * 2-审核不通过
     *
     * 3-其他非审核状态
     *
     * 4-编辑中，未提交审核
     */
    @Json(name = "auditResult")
    val auditResult: Int,
    @Json(name = "versionCode")
    val versionCode: Long,
    @Json(name = "versionName")
    val versionName: String,
) {

    fun toMarketState(): MarketInfo {
        val state = when (auditResult) {
            0 -> ReviewState.UnderReview
            1 -> ReviewState.Online
            2 -> ReviewState.Rejected
            else -> ReviewState.Unknown
        }
        return MarketInfo(
            reviewState = state,
            lastVersion = MarketInfo.Version(versionCode, versionName)
        )
    }
}