package apk.dispatcher.channel.huawei

import apk.dispatcher.channel.MarketState
import apk.dispatcher.channel.ReviewState
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HWAppInfoResp(
    @Json(name = "ret")
    val result: HWResult,
    @Json(name = "appInfo")
    val appInfo: AppInfo,
) {
    @JsonClass(generateAdapter = false)
    data class AppInfo(
        /**
         * 应用状态。
         *
         * 0：已上架
         * 1：上架审核不通过
         * 2：已下架（含强制下架）
         * 3：待上架，预约上架
         * 4：审核中
         * 5：升级中
         * 6：申请下架
         * 7：草稿
         * 8：升级审核不通过
         * 9：下架审核不通过
         * 10：应用被开发者下架
         * 11：撤销上架
         */
        @Json(name = "releaseState")
        val releaseState: Int,
        @Json(name = "versionCode")
        val versionCode: Long,
        @Json(name = "versionNumber")
        val versionNumber: String,
        /**
         *
         * 在架版本版本号
         */
        @Json(name = "onShelfVersionNumber")
        val onShelfVersionNumber: String,
    ) {
        fun toAppState(): MarketState {
            val reviewState = when (releaseState) {
                0 -> ReviewState.Online
                4, 5 -> ReviewState.UnderReview
                8 -> ReviewState.Rejected
                else -> ReviewState.Unknown
            }
            return MarketState(
                reviewState = reviewState,
                lastVersionName = versionNumber,
                lastVersionCode = versionCode
            )
        }
    }
}