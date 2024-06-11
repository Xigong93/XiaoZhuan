package apk.dispatcher

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC

object OkhttpFactory {

    private val logInterceptor = HttpLoggingInterceptor().apply { setLevel(BASIC) }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .build()

    fun default() = okHttpClient
}