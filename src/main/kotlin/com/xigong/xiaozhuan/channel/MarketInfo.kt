package com.xigong.xiaozhuan.channel

/**
 * APP在应用市场的状态
 */
data class MarketInfo(

    /** 审核状态 */
    val reviewState: ReviewState,
    /** 是否允许提交新版本 */
    val enableSubmit: Boolean = reviewState != ReviewState.UnderReview,
    /**
     * 最新版本号
     */
    val lastVersionCode: Long,
    /**
     * 最新版本名称
     */
    val lastVersionName: String
)
