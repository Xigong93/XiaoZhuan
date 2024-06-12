package apk.dispatcher.channel.vivo

import java.nio.charset.Charset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


object VIVOApiSigner {
    /**
     * 获取加密验签
     */
    fun getSignParams(
        accessKey: String,
        accessSecret: String,
        method: String,
        originParams: Map<String, String>
    ): Map<String, String> {
        val params = originParams.toMutableMap()
        //公共参数
        params["access_key"] = accessKey
        params["timestamp"] = System.currentTimeMillis().toString()
        params["method"] = method
        params["v"] = "1.0"
        params["sign_method"] = "HMAC-SHA256"
        params["format"] = "json"
        params["target_app_key"] = "developer"
        val data = getUrlParamsFromMap(params)
        params["sign"] = hmacSHA256(data, accessSecret)
        return params
    }

    /**
     * 根据传入的map，把map里的key   value转换为接口的请求参数，并给参数按ascii码排序
     *
     * @param paramsMap 传入的map
     * @return 按ascii码排序的参数键值对拼接结果
     */
    private fun getUrlParamsFromMap(paramsMap: Map<String, String>): String {
        val keysList: List<String> = ArrayList(paramsMap.keys)
        Collections.sort(keysList)
        val sb = StringBuilder()
        val paramList: MutableList<String> = ArrayList()
        for (key in keysList) {
            val `object` = paramsMap[key] ?: continue
            val value = "$key=$`object`"
            paramList.add(value)
        }
        return java.lang.String.join("&", paramList)
    }

    /**
     * HMAC_SHA256 验签加密
     * @param data 需要加密的参数
     * @param key 签名密钥
     * @return String 返回加密后字符串
     */
    private fun hmacSHA256(data: String, key: String): String {

        val secretByte = key.toByteArray(Charset.forName("UTF-8"))
        val signingKey = SecretKeySpec(secretByte, "HmacSHA256")
        val mac: Mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val dataByte = data.toByteArray(Charset.forName("UTF-8"))
        val by: ByteArray = mac.doFinal(dataByte)
        return byteArr2HexStr(by)

    }

    /**
     * HMAC_SHA256加密后的数组进行16进制转换
     */
    private fun byteArr2HexStr(bytes: ByteArray): String {
        val length = bytes.size
        //每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        val sb = java.lang.StringBuilder(length * 2)
        for (i in 0 until length) {
            //将得到的字节转16进制
            val strHex = Integer.toHexString(bytes[i].toInt() and 0xFF)
            // 每个字节由两个字符表示，位数不够，高位补0
            sb.append(if ((strHex.length == 1)) "0$strHex" else strHex)
        }
        return sb.toString()
    }
}