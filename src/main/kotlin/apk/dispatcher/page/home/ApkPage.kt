package apk.dispatcher.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import apk.dispatcher.config.ApkConfig
import apk.dispatcher.page.upload.UploadParam
import apk.dispatcher.style.AppColors
import apk.dispatcher.widget.Section
import apk.dispatcher.widget.TwoPage
import apk.dispatcher.widget.UpdateDescView


@Composable
fun ApkPage(appId: String, startUpload:(UploadParam)->Unit) {
    val apkVM = viewModel { ApkVM() }
    LaunchedEffect(true) {
        apkVM.loadApkConfig(appId)
    }
    val apkConfig = apkVM.apkConfigState.value
    if (apkConfig != null) {
        TwoPage(
            leftPage = { LeftPage(apkConfig, apkVM) },
            rightPage = { ChannelGroup(apkVM, startUpload) },
        )
    }
}


@Composable
private fun ColumnScope.LeftPage(apkConfig: ApkConfig, viewModel: ApkVM) {
    val dividerHeight = 30.dp
    Section("Apk信息") {
        ApkInfoBox(apkConfig)
    }
    Spacer(Modifier.height(dividerHeight))
    Section("选择文件") {

        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.cardBackground)
                .padding(16.dp)
        ) {
            val apkInfo = viewModel.getApkInfoState().value
            val version = apkInfo?.versionName?.let { "v${it}" }
            val apkPath = viewModel.getApkDirState().value?.path ?: ""
            item("文件:", apkPath ?: "")
            Spacer(Modifier.height(12.dp))
            item("版本:", version ?: "")

            Spacer(Modifier.height(12.dp))
            item("大小:", viewModel.getFileSize())
        }
        Spacer(Modifier.height(12.dp))
        Button(
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = AppColors.primary,
            ),
            onClick = {
                if (apkConfig.enableChannel) {
                    viewModel.selectedApkDir()
                } else {
                    viewModel.selectApkFile()
                }
            }) {
            val text = if (apkConfig.enableChannel) "选择Apk文件夹" else "选择Apk文件"
            Text(text, color = Color.White, fontSize = 14.sp)
        }

    }
    Spacer(Modifier.height(dividerHeight))
    Section("更新描述") {
        UpdateDescView(viewModel.updateDesc)
    }
}


@Composable
private fun ApkInfoBox(apkConfig: ApkConfig) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.cardBackground)
            .padding(16.dp)
    ) {

        item("名称:", apkConfig.name)
        Spacer(Modifier.height(12.dp))
        item("包名:", apkConfig.applicationId)
        Spacer(Modifier.height(12.dp))
        item("渠道包:", if (apkConfig.enableChannel) "是" else "否")
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



