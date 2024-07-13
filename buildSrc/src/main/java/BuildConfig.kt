import com.google.gson.Gson


data class BuildConfig(
    val versionCode: Long,
    val versionName: String,
    val packageId: String,
    val appName: String,
    val release: Boolean
) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}
