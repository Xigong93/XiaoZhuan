// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import apk.dispatcher.BuildConfig
import apk.dispatcher.log.AppLogger
import apk.dispatcher.log.CrashHandler
import apk.dispatcher.page.AppNavigation
import apk.dispatcher.widget.ConfirmDialog
import apk.dispatcher.widget.RootWindow


fun main() {
    CrashHandler.install()
    AppLogger.info("main", "App启动")
    BuildConfig.print()
    application {
        val windowState = rememberWindowState(
            width = 1280.dp, height = 960.dp,
            position = WindowPosition(Alignment.Center)
        )

        var exitDialog by remember { mutableStateOf(false) }

        Window(
            title = BuildConfig.appName,
            icon = painterResource(BuildConfig.ICON),
            resizable = false,
            transparent = true,
            undecorated = true,
            state = windowState,
            onCloseRequest = {
                exitDialog = true
            }
        ) {
            RootWindow(miniClick = {
                windowState.isMinimized = true
            }, closeClick = {
                exitDialog = true
            }) {
                AppNavigation()
                if (exitDialog) {
                    ConfirmDialog("确定退出软件吗？",
                        onConfirm = {
                            exitDialog = false
                            AppLogger.info("main", "App关闭")
                            exitApplication()
                        }, onDismiss = {
                            exitDialog = false
                        })
                }
            }
        }
    }
}
