package apk.dispatcher

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object RetrofitFactory {
    inline fun <reified T> create(domain: String): T {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .callFactory(OkhttpFactory.default())
            .baseUrl(domain)
            .build()
            .create()
    }

}