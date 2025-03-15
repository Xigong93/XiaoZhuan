package com.xigong.xiaozhuan.channel.oppo

import com.xigong.xiaozhuan.channel.MarketInfo
import com.xigong.xiaozhuan.channel.ReviewState
import com.google.gson.JsonObject

data class OPPOAppInfo(val obj: JsonObject) {

    /**
     * 一句话介绍
     */
    val summary: String = obj.get("summary").asString

    /**
     * 软件介绍
     */
    val detailDesc: String = obj.get("detail_desc").asString


    val versionCode: Long = obj.get("version_code").asLong

    val versionName: String = obj.get("version_name").asString

    /**
     * 审核状态
     */
    val reviewStatus: Int = obj.get("audit_status").asInt

    /**
     * 隐私政策网址
     */
    val privacyUrl: String = obj.get("privacy_source_url").asString

    /**
     * 二级分类id
     */
    val secondCategory: String = obj.get("ver_second_category_id").asString

    /**
     * 三级分类id
     */
    val thirdCategory: String = obj.get("ver_third_category_id").asString


    val iconUrl: String = obj.get("icon_url").asString
    val picUrl: String = obj.get("pic_url").asString

    /**
     * 测试附加说明
     */
    val testDesc: String = obj.get("test_desc")?.asString ?: ""

    /**
     * 商务联系方式
     */
    val businessUsername: String = obj.get("business_username")?.asString ?: ""
    val businessEmail: String = obj.get("business_email")?.asString ?: ""
    val businessMobile: String = obj.get("business_mobile")?.asString ?: ""

    /**
     * 纸质版软著
     */
    val copyrightUrl: String = obj.get("copyright_url")?.asString ?: ""

    /**
     * 电子版软著
     */
    val electronicCertUrl: String = obj.get("electronic_cert_url")?.asString ?: ""

    fun toMarketState(): MarketInfo {
        val state = when (reviewStatus) {
            111 -> ReviewState.Online
            444 -> ReviewState.Rejected
            else -> ReviewState.UnderReview
        }
        return MarketInfo(
            reviewState = state,
            lastVersion = MarketInfo.Version(versionCode, versionName)
        )
    }
}