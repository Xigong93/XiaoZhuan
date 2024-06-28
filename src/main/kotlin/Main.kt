// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import apk.dispatcher.log.AppLogger
import apk.dispatcher.log.CrashHandler
import apk.dispatcher.page.PageNavHost
import apk.dispatcher.widget.Toast


fun main() {
    CrashHandler.install()
    AppLogger.info("main", "App启动")
    application {
        val windowState = rememberWindowState(
            width = 1000.dp, height = 800.dp,
            position = WindowPosition(Alignment.Center)
        )
        Window(
            title = "软件版本更新",
            resizable = false,
            state = windowState,
            onCloseRequest = {
                AppLogger.info("main", "App关闭")
                exitApplication()
            }
        ) {
            RootWindow()
        }
    }
}

@Composable
fun RootWindow() {
    PageNavHost()
    Toast.install()
}