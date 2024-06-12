package apk.dispatcher.channel.mi

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.security.PublicKey
import java.security.Security
import java.security.cert.CertificateFactory
import javax.crypto.Cipher

object MiApiSigner {
    /**
     * 以下四项为接口参数加密算法X509用到的参数
     */

    private const val KEY_SIZE: Int = 1024

    private const val GROUP_SIZE: Int = KEY_SIZE / 8

    private const val ENCRYPT_GROUP_SIZE: Int = GROUP_SIZE - 11

    private const val KEY_ALGORITHM: String = "RSA/NONE/PKCS1Padding"


    /**
     * 加载BC库
     */
    init {
        Security.addProvider(BouncyCastleProvider());
    }



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
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    fun encrypt(content: String, publicKey: String): String {
        val data = content.toByteArray()
        val baos = ByteArrayOutputStream()
        val segment = ByteArray(ENCRYPT_GROUP_SIZE)
        var idx = 0
        val cipher = Cipher.getInstance(KEY_ALGORITHM, "BC")
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKeyByX509Cer(publicKey))
        while (idx < data.size) {
            val remain = data.size - idx
            val segsize = Math.min(remain, ENCRYPT_GROUP_SIZE)
            System.arraycopy(data, idx, segment, 0, segsize)
            baos.write(cipher.doFinal(segment, 0, segsize))
            idx += segsize
        }
        return Hex.encodeHexString(baos.toByteArray())
    }



}