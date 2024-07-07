package apk.dispatcher.util

import apk.dispatcher.log.AppLogger
import apk.dispatcher.widget.Toast
import java.awt.Desktop
import java.net.URI

fun browser(url: String) {
    try {
        Desktop.getDesktop().browse(URI(url))
    } catch (e: Exception) {
        AppLogger.error("打开链接", "打开链接失败", e)
        Toast.show("打开链接失败")
    }
}
