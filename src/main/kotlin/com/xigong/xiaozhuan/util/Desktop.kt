package com.xigong.xiaozhuan.util

import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.widget.Toast
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
