package apk.dispatcher.mi

import apk.dispatcher.OkhttpFactory
import apk.dispatcher.util.ProgressChange
import apk.dispatcher.util.ProgressRequestBody
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.security.PublicKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.util.*
import javax.crypto.Cipher


/**
 * 小米应用市场Api
 * @param publicKey 公钥
 * @param privateKey 私钥
 */
class MiMarketApi(
    private val miAccount: String,
    publicKey: String,
    private val privateKey: String
) {
    private val httpClient = OkhttpFactory.default()

    private val publicKey = getPublicKeyByX509Cer(publicKey)


    /**
     * 读取公钥
     *
     * @param cerFilePath 本地公钥存放的文件目录
     * @return 返回公钥
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getPublicKeyByX509Cer(publicKey: String): PublicKey {
        try {
            val factory = CertificateFactory.getInstance("X.509")
            val cert = factory.generateCertificate(publicKey.byteInputStream())
            return cert.publicKey
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * 使用公钥加密
     *
     * @param str
     * @param publicKey
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    private fun encryptByPublicKey(str: String, publicKey: PublicKey): String {
        val data = str.toByteArray()
        val baos = ByteArrayOutputStream()
        val segment = ByteArray(ENCRYPT_GROUP_SIZE)
        var idx = 0
        val cipher = Cipher.getInstance(KEY_ALGORITHM, "BC")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        while (idx < data.size) {
            val remain = data.size - idx
            val segsize = Math.min(remain, ENCRYPT_GROUP_SIZE)
            System.arraycopy(data, idx, segment, 0, segsize)
            baos.write(cipher.doFinal(segment, 0, segsize))
            idx += segsize
        }
        return Hex.encodeHexString(baos.toByteArray())
    }

    /*
     * 获取文件md5
     */
    @Throws(java.lang.Exception::class)
    private fun getFileMD5(file: File): String {
        try {
            FileInputStream(file).use { fis ->
                return DigestUtils.md5Hex(fis)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * 获取App信息
     * @param miAccount 小米应用市场账号（邮箱)
     * @param packageName 应用包名
     */
    suspend fun getAppInfo(packageName: String): MiAppInfoResp.MiAppInfo =
        withContext(Dispatchers.IO) {
            val requestData = JSONObject().apply {
                put("userName", miAccount)
                put("packageName", packageName)
            }
            val sig = JSONObject().apply {
                put("password", privateKey)
                put("sig", JSONArray().apply {
                    put(JSONObject().apply {
                        put("name", "RequestData")
                        put("hash", DigestUtils.md5Hex(requestData.toString()))
                    })
                })
            }
            val body = FormBody.Builder()
                .add("RequestData", requestData.toString())
                .add("SIG", encryptByPublicKey(sig.toString(), publicKey))
                .build()
            val request = Request.Builder().url(QUERY).post(body).build()
            val response = httpClient.newCall(request).execute()
            check(response.isSuccessful)
            val respBody = requireNotNull(response.body)
            val infoResp = MiAppInfoResp.adapter.fromJson(respBody.string())
            checkNotNull(infoResp)
            infoResp.packageInfo
        }

    /**
     * 上传Apk
     */
    suspend fun uploadApk(
        apkFile: File,
        appInfo: MiAppInfoResp.MiAppInfo,
        updateDesc: String,
        progressChange: ProgressChange
    ) =
        withContext(Dispatchers.IO) {
            val requestData = JSONObject().apply {
                put("userName", miAccount)
                put("synchroType", 1)// 0：新增app; 1：更新app; 2：app信息修改
                put("appInfo", JSONObject().apply {
                    put("appName", appInfo.appName)
                    put("packageName", appInfo.packageName)
                    put("updateDesc", updateDesc)
                })
            }

            val sig = JSONObject().apply {
                put("password", privateKey)
                put("sig", JSONArray().apply {
                    put(JSONObject().apply {
                        put("name", "RequestData")
                        put("hash", DigestUtils.md5Hex(requestData.toString()))
                    })
                    put(JSONObject().apply {
                        put("name", "apk")
                        put("hash", getFileMD5(apkFile))
                    })
                })
            }
            val apkBody = ProgressRequestBody(
                mediaType = "application/octet-stream".toMediaType(),
                file = apkFile,
                progressChange = progressChange
            )
            val body = MultipartBody.Builder()
                .addFormDataPart("apk", "", apkBody)
                .addFormDataPart("RequestData", requestData.toString())
                .addFormDataPart("SIG", encryptByPublicKey(sig.toString(), publicKey))
                .build()
            val request = Request.Builder().url(PUSH).post(body).build()
            val response = httpClient.newCall(request).execute()
            check(response.isSuccessful)
            val resultText = requireNotNull(response.body).string()
            val result = JsonParser.parseString(resultText).asJsonObject
            check(result.get("result").asInt == 0) {
                "上传失败，$resultText"
            }
        }

    private companion object {
        /**
         * 自动发布接口域名
         */

        const val DOMAIN: String = "http://api.developer.xiaomi.com/devupload"

        /**
         * 推送普通apk Url前缀
         */

        const val PUSH: String = "$DOMAIN/dev/push"

        /**
         * 推送渠道包Url前缀
         */

        const val PUSH_CHANNEL_APK: String = "$DOMAIN/dev/pushChannelApk"

        /**
         * 查询app状态的Url前缀
         */

        const val QUERY: String = "$DOMAIN/dev/query"

        /**
         * 查询应用分类Url前缀
         */

        const val CATEGORY: String = "$DOMAIN/dev/category"

        /**
         * 以下四项为接口参数加密算法X509用到的参数
         */

        const val KEY_SIZE: Int = 1024

        const val GROUP_SIZE: Int = KEY_SIZE / 8

        const val ENCRYPT_GROUP_SIZE: Int = GROUP_SIZE - 11

        const val KEY_ALGORITHM: String = "RSA/NONE/PKCS1Padding"


        /**
         * 加载BC库
         */
        init {

            Security.addProvider(BouncyCastleProvider());
        }
    }
}