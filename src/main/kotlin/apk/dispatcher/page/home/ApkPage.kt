package apk.dispatcher.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.style.AppColors
import apk.dispatcher.widget.Section
import apk.dispatcher.widget.Toast
import apk.dispatcher.widget.TwoPage
import apk.dispatcher.widget.UpdateDescView
import javax.swing.JFileChooser
import javax.swing.JFileChooser.*
import javax.swing.filechooser.FileNameExtensionFilter


@Composable
fun ApkPage(apkConfig: ApkConfig) {
    val apkViewModel = remember(apkConfig) { ApkViewModel(apkConfig) }
    TwoPage(
        leftPage = { LeftPage(apkViewModel) },
        rightPage = { ChannelGroupPage(apkViewModel) },
    )
}


@Composable
private fun ColumnScope.LeftPage(viewModel: ApkViewModel) {
    val dividerHeight = 30.dp
    Section("Apk信息") {
        ApkInfoBox(viewModel)
    }
    Spacer(Modifier.height(dividerHeight))
    Section("选择文件") {
        Button(
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = AppColors.primary,
            ),
            onClick = {
                if (viewModel.apkConfig.enableChannel) {
                    viewModel.selectedApkDir()
                } else {
                    viewModel.selectApkFile()
                }
            }) {
            val text = if (viewModel.apkConfig.enableChannel) "选择Apk文件夹" else "选择Apk文件"
            Text(text, color = Color.White, fontSize = 14.sp)
        }
        val apkPath = viewModel.getApkDirState().value?.path ?: ""
        if (apkPath.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(apkPath, color = AppColors.fontGray, fontSize = 12.sp)
        }
    }
    Spacer(Modifier.height(dividerHeight))
    Section("更新描述") {
        UpdateDescView(viewModel.updateDesc)
    }
}


@Composable
private fun ApkInfoBox(apkViewModel: ApkViewModel) {
    Column(
        modifier = Modifier.width(300.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.cardBackground)
            .padding(16.dp)
    ) {
        val apkInfo = apkViewModel.getApkInfoState().value
        val version = apkInfo?.versionName?.let { "v${it}" }
        val applicationId = apkInfo?.applicationId ?: ""
        item("名称:", apkViewModel.apkConfig.name)
        Spacer(Modifier.height(12.dp))

        item("包名:", applicationId)

        Spacer(Modifier.height(12.dp))

        item("版本:", version ?: "")

        Spacer(Modifier.height(12.dp))
        item("大小:", apkViewModel.getFileSize())

    }
}

@Composable
private fun item(title: String, desc: String) {
    Row {
        Text(
            title,
            color = AppColors.fontGray,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            desc,
            color = AppColors.fontBlack,
            fontSize = 14.sp
        )
    }
}



