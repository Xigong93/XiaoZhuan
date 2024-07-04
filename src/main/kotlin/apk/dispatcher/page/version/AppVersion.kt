package apk.dispatcher.page.version

data class AppVersion(
    val versionCode: Long,
    val versionName: String
) : Comparable<AppVersion> {
    companion object {

        @Throws
        fun from(versionName: String): AppVersion {
            val vName = versionName.lowercase().trim().trim('v')
            val pieces = vName.split('.')
            check(pieces.size == 3) { "无效的App版本,${versionName}" }
            val major = pieces[0].toInt()
            val minor = pieces[1].toInt()
            val revision = pieces[2].toInt()
            require(major in 0..999) { "major must in [0,999],but is $major" }
            require(minor in 0..99) { "minor must in [0,99],but is $minor" }
            require(revision in 0..99) { "revision must in [0,99],but is $revision" }
            val vCode = major * 10000 + minor * 100 + revision
            return AppVersion(vCode.toLong(), vName)
        }
    }

    override fun compareTo(other: AppVersion): Int {
        return versionCode.compareTo(other.versionCode)
    }
}
