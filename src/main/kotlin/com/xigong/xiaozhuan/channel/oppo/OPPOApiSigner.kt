package com.xigong.xiaozhuan.channel.oppo

import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object OPPOApiSigner {


    /**
     * 对请求参数进行签名
     * @param secret
     * @param paramsMap
     * @return String
     * @throws IOException
     */
    @Throws(IOException::class)
    fun sign(secret: String, paramsMap: Map<String, String>): String {
        val keysList: List<String> = ArrayList(paramsMap.keys)
        Collections.sort(keysList)
        val paramList: MutableList<String> = ArrayList()
        for (key in keysList) {
            val `object` = paramsMap[key] ?: continue
            val value = "$key=$`object`"
            paramList.add(value)
        }
        val signStr = java.lang.String.join("&", paramList)
        return hmacSHA256(signStr, secret)
    }

    /**
     * HMAC_SHA256 计算签名
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
     * 字节数组转换为十六进制
     * @param bytes
     * @return String
     */
    private fun byteArr2HexStr(bytes: ByteArray): String {
        val length = bytes.size
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        val sb = java.lang.StringBuilder(length * 2)
        for (i in 0 until length) {
            // 将得到的字节转16进制
            val strHex = Integer.toHexString(bytes[i].toInt() and 0xFF)
            // 每个字节由两个字符表示，位数不够，高位补0
            sb.append(if ((strHex.length == 1)) "0$strHex" else strHex)
        }
        return sb.toString()
    }

}