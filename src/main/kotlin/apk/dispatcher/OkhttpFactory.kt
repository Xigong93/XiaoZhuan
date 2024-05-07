package apk.dispatcher

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

object OkhttpFactory {

    private val logInterceptor = HttpLoggingInterceptor().apply { setLevel(BODY) }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .build()

    fun default() = okHttpClient
}