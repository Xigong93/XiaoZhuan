@file:JvmName("Main")

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
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
        var showExitDialog by remember { mutableStateOf(false) }
        Window(
            title = BuildConfig.appName,
            icon = painterResource(BuildConfig.ICON),
            state = rememberWindowState(
                width = 1280.dp, height = 960.dp,
                position = WindowPosition(Alignment.Center)
            ),
            onCloseRequest = {
                showExitDialog = true
            }
        ) {
            RootWindow(closeClick = { showExitDialog = true }) {
                AppNavigation()
            }
            if (showExitDialog) {
                exitDialog {
                    showExitDialog = false
                }
            }
        }

    }
}

@Composable
private fun ApplicationScope.exitDialog(requestDismiss: () -> Unit) {
    ConfirmDialog(
        "确定退出软件吗？",
        onDismiss = requestDismiss,
        onConfirm = {
            requestDismiss()
            AppLogger.info("main", "App关闭")
            exitApplication()
        },
    )
}

