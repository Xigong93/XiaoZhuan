package com.xigong.xiaozhuan.page.version

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xigong.xiaozhuan.Api
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import com.xigong.xiaozhuan.util.browser
import com.xigong.xiaozhuan.widget.NegativeButton
import com.xigong.xiaozhuan.widget.PositiveButton


@Composable
fun NewVersionDialog() {
    val viewModel = remember { AppVersionVM() }
    var showDialog by remember { mutableStateOf(true) }
    val newVersion = (viewModel.versionState.value as? GetVersionState.New)?.version
    if (newVersion != null && showDialog) {
        Content(newVersion) { showDialog = false }
    }
}

@Preview
@Composable
private fun NewVersionDialogPreview() {
    val version = AppVersion(100, versionName = "1.2.0", "修复了已知bug")
    Content(version) {

    }
}

@Composable
private fun Content(version: AppVersion, onDismiss: () -> Unit) {

    Dialog({ }, properties = remember { DialogProperties() }) {

        Column(
            modifier = Modifier
                .width(600.dp)
                .clip(RoundedCornerShape(AppShapes.largeCorner))
                .background(Color.White)
                .padding(20.dp),
        ) {
            Text(
                "发现新版本 v${version.versionName}",
                color = AppColors.fontBlack,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(40.dp))
            Text(version.desc, color = AppColors.fontGray, fontSize = 14.sp)

            Spacer(Modifier.height(40.dp))
            Row {
                Spacer(Modifier.weight(1f))
                NegativeButton("忽略", modifier = Modifier.width(100.dp), onClick = {
                    onDismiss()
                })
                Spacer(Modifier.width(12.dp))
                PositiveButton("下载更新", onClick = {
                    browser("${Api.GITHUB_URL}/releases")
                    onDismiss()
                })
            }

        }
    }

}