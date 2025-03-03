package com.xigong.xiaozhuan.channel

/**
 * APP在应用市场的状态
 */
data class MarketInfo(

    /** 审核状态 */
    val reviewState: ReviewState,
    /** 是否允许提交新版本 */
    val enableSubmit: Boolean = true,
    /** 最新版本号 */
    val lastVersion: Version? = null
) {
    data class Version(
        val code: Long,
        val name: String,
    )
}
