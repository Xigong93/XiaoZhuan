package apk.dispatcher

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiFactory {

    val default: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


    inline fun <reified T> getAdapter(): JsonAdapter<T> {
        return default.adapter(T::class.java)
    }
}