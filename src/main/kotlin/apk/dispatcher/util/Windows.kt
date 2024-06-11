package apk.dispatcher.util

import java.util.*

/**
 * 当前系统是不是windows
 */
fun isWindows(): Boolean {
    return System.getProperty("os.name")
        .lowercase(Locale.getDefault())
        .contains("windows")
}
