package apk.dispatcher.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.page.upload.UploadDialog
import apk.dispatcher.widget.ConfirmDialog
import apk.dispatcher.widget.Section

/**
 * 渠道页面
 */
@Composable
fun ChannelGroupPage(viewModel: ApkViewModel) {
    Column(modifier = Modifier.padding(20.dp)) {
        Section("渠道") {
            Column() {
                Spacer(modifier = Modifier.height(15.dp))
                viewModel.channels.withIndex().forEach { (index, chan) ->
                    val selected = viewModel.selectedChannels.contains(chan.channelName)
                    val name = chan.channelName
                    val taskLauncher = viewModel.taskLaunchers.first { it.name == name }
                    val apkFileState = taskLauncher.getApkFileState()
                    val desc = apkFileState.value?.name
                    ChannelView(selected, name, desc) { checked ->
                        viewModel.selectChannel(name, checked)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Box(modifier = Modifier.fillMaxWidth()) {
            val allSelected = viewModel.allChannelSelected()
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clip(AppShapes.roundButton)
                .clickable {
                    viewModel.selectAll(allSelected.not())
                }
                .padding(end = 12.dp)) {
                Checkbox(
                    allSelected,
                    onCheckedChange = { all ->
                        viewModel.selectAll(all)
                    },
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                )
                Text("全选")
            }
            var showUploading by remember { mutableStateOf(false) }
            if (showUploading) {
                UploadDialog(viewModel) { showUploading = false }
            }
            var showAlert by remember { mutableStateOf(false) }
            if (showAlert) {
                showConfirmDialog(viewModel,
                    onConfirm = {
                        showAlert = false
                        showUploading = true
                    }, onDismiss = {
                        showAlert = false

                    }
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(AppColors.primary),
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    if (viewModel.checkForm()) {
                        showAlert = true
                    }
                }
            ) {

                Text(
                    "发布更新",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}


@Composable
private fun showConfirmDialog(viewModel: ApkViewModel, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val apkInfo = viewModel.getApkInfoState().value ?: return
    val selectedChannels = viewModel.selectedChannels
    val message = buildString {
        append("包名:  ${apkInfo.applicationId}")
        append("\n")
        append("版本:  ${apkInfo.versionName}(${apkInfo.versionCode})")
        append("\n")
        append("渠道:  ${selectedChannels.joinToString(",")}")
    }


    ConfirmDialog(
        message, "确定上传",
        onConfirm = {
            onConfirm()
        }, onDismiss = {
            onDismiss()
        }
    )
}


@Preview
@Composable
private fun ChannelViewPreview() {
//    Column(modifier = Modifier.background(AppColors.pageBackground).padding(10.dp)) {
//        val huawei = Channel("华为", "星题库-v5.30.0-HUAWEI.apk", ChannelState.Success, true)
//        ChannelView(huawei, onCheckChange = {})
//        Spacer(modifier = Modifier.height(10.dp))
//        val xiaomi = Channel("小米", "星题库-v5.30.0-MI.apk", ChannelState.Uploading(45), false)
//        ChannelView(xiaomi, onCheckChange = {})
//        Spacer(modifier = Modifier.height(10.dp))
//        val oppo = Channel("OPPO", "星题库-v5.30.0-OPPO.apk", ChannelState.Success, true)
//        ChannelView(oppo, onCheckChange = {})
//        Spacer(modifier = Modifier.height(10.dp))
//        val vivo = Channel("VIVO", "星题库-v5.30.0-VIVO.apk", ChannelState.Error("网络错误"), true)
//        ChannelView(vivo, onCheckChange = {})
//    }
}

@Composable
private fun ChannelView(
    selected: Boolean,
    name: String,
    desc: String?,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable {
                onCheckChange(!selected)
            }
            .padding(16.dp)
    ) {
        Checkbox(
            selected,
            onCheckedChange = onCheckChange,
            colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
        )
        Text(
            name,
            fontSize = 14.sp,
            color = AppColors.fontBlack,
            modifier = Modifier.requiredWidthIn(min = 50.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            desc ?: "",
            fontSize = 12.sp,
            color = AppColors.fontGray
        )

    }
}


sealed class ChannelState {
    object Waiting : ChannelState()
    class Processing(val action: String) : ChannelState()

    /**
     * @param progress 取值范围[0,100]
     */
    class Uploading(val progress: Int) : ChannelState()
    object Success : ChannelState()
    class Error(val message: String) : ChannelState()

    val finish: Boolean get() = this == Success || this is Error
    val success: Boolean get() = this == Success
}
