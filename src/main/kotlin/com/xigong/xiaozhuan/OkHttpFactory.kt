package com.xigong.xiaozhuan

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import java.net.InetSocketAddress
import java.net.Proxy
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

// 有几个平台的接口响应非常慢，所以这个时间要设置的大一些
private val timeout = 30.seconds.toJavaDuration()

private const val DEBUG_NETWORK = false

object OkHttpFactory {


    private val okHttpClient = OkHttpClient
        .Builder()
        .readTimeout(timeout)
        .writeTimeout(timeout)
        .build()

    fun default() = if (DEBUG_NETWORK && BuildConfig.debug) debugClient() else okHttpClient
}

private fun debugClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        setLevel(BASIC)
    }
    // 配置代理，并信任所有证书
    val sslContext = SSLContext.getInstance("SSL")
    val trustManager = getTrustManager()
    sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
    return OkHttpClient.Builder()
        .addInterceptor(logging)
        .readTimeout(timeout)
        .writeTimeout(timeout)
        .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(8080)))
        .sslSocketFactory(sslContext.socketFactory, trustManager)
        .hostnameVerifier { _, _ -> true }
        .build()
}

private fun getTrustManager() = object : X509TrustManager {

    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}

