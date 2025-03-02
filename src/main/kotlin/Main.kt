@file:JvmName("Main")

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
import com.xigong.xiaozhuan.BuildConfig
import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.log.CrashHandler
import com.xigong.xiaozhuan.page.AppNavigation
import com.xigong.xiaozhuan.widget.ConfirmDialog
import com.xigong.xiaozhuan.widget.RootWindow

fun main() {
    CrashHandler.install()
    AppLogger.info("main", "App启动")
    BuildConfig.print()
    application {
        var exitDialog by remember { mutableStateOf(false) }
        val state = rememberWindowState(
            width = 1080.dp, height = 760.dp,
            position = WindowPosition(Alignment.Center)
        )
        Window(
            title = BuildConfig.appName,
            icon = painterResource(BuildConfig.ICON),
            resizable = true,
            transparent = true,
            undecorated = true,
            state = state,
            onCloseRequest = {
                exitDialog = true
            }
        ) {
            RootWindow(state = state, closeClick = { exitDialog = true }) {
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
