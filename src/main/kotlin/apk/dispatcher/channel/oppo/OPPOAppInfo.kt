package apk.dispatcher.channel.oppo

import com.google.gson.JsonObject

class OPPOAppInfo(obj: JsonObject) {

    /**
     * 一句话介绍
     */
    val summary: String = obj.get("summary").asString

    /**
     * 软件介绍
     */
    val detailDesc: String = obj.get("detail_desc").asString


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
     * 软件的版权证明
     */
    val copyrightUrl: String = obj.get("copyright_url")?.asString ?: ""

}