package apk.dispatcher.page.about

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.BuildConfig
import apk.dispatcher.log.AppLogger
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.widget.Toast
import java.awt.Desktop
import java.net.URI

@Composable
fun AboutSoftDialog(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .width(600.dp)
            .clip(RoundedCornerShape(AppShapes.largeCorner))
            .background(Color.White)
            .padding(20.dp),
    ) {
        Text("关于软件", color = AppColors.fontBlack, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(Modifier.height(26.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(BuildConfig.ICON),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.width(30.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Content()

                Spacer(Modifier.height(26.dp))
                Row {
                    Spacer(Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .clip(AppShapes.roundButton)
                            .background(AppColors.primary)
                            .clickable {
                                onDismiss()
                            }
                    ) {
                        Text(
                            "关闭",
                            color = Color.White,
                            letterSpacing = 3.sp,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AboutSoftDialogPreview() {
    Box(Modifier.background(Color.Gray).fillMaxSize()) {
        AboutSoftDialog { }
    }
}

@Suppress("SpellCheckingInspection")
private const val GITEE_URL = "https://gitee.com/xigong93/ApkDispatcher"
private const val GITHUB_URL = "https://github.com/xigong93/ApkDispatcher"

@Composable
private fun Content() {
    val dividerHeight = 18.dp
    Text(BuildConfig.appName, color = AppColors.fontBlack, fontSize = 14.sp)
    Spacer(Modifier.height(dividerHeight))

    Text(
        "版本号：v${BuildConfig.versionName}",
        color = AppColors.fontGray,
        fontSize = 14.sp
    )
    Spacer(Modifier.height(dividerHeight))
    Text(
        "一键上传Apk到多个应用市场，开源、免费",
        color = AppColors.fontGray,
        fontSize = 14.sp
    )
    Spacer(Modifier.height(dividerHeight))

    @Suppress("SpellCheckingInspection")
    ClickText("Gitee 地址：$GITEE_URL", GITEE_URL)
    Spacer(Modifier.height(dividerHeight))
    ClickText("Github 地址：$GITHUB_URL", GITHUB_URL)
}

@Composable
private fun ClickText(text: String, url: String) {
    val source = remember { MutableInteractionSource() }
    val isHover = source.collectIsHoveredAsState().value
    val color = if (isHover) AppColors.primary else AppColors.fontGray
    Text(
        text,
        color = color,
        fontSize = 14.sp,
        modifier = Modifier
            .hoverable(source)
            .background(Color.Transparent)
            .clickable { browser(url) }
    )
}

private fun browser(url: String) {
    try {
        Desktop.getDesktop().browse(URI(url))
    } catch (e: Exception) {
        AppLogger.error("打开链接", "打开链接失败", e)
        Toast.show("打开链接失败")
    }
}
