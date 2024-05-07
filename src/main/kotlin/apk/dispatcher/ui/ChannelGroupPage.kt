package apk.dispatcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors
import apk.dispatcher.widget.Section

/**
 * 渠道页面
 */
class ChannelGroupPage(private val viewModel: ApkViewModel) {


    @Composable
    fun render() {
        Column(modifier = Modifier.padding(20.dp)) {
            Section("渠道") {
                Column() {
                    Spacer(modifier = Modifier.height(15.dp))
                    viewModel.channels.withIndex().forEach { (index, chan) ->
                        val selected = viewModel.selectedChannels.contains(chan.channelName)
                        val name = chan.channelName
                        val taskLauncher = viewModel.taskLaunchers.first { it.name == name }
                        val state = taskLauncher.getChannelState()
                        val apkFileState = taskLauncher.getApkFileState()
                        val desc = apkFileState.value?.name
                        ChannelView(selected,name, desc, state.value) { checked ->

                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val allSelected = viewModel.selectedChannels.containsAll(viewModel.channels.map { it.channelName })
                    Checkbox(
                        allSelected,
                        onCheckedChange = { all -> },
                        colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
                    )
                    Text("全选")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(AppColors.primary),
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { viewModel.startDispatch() }
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
    state: ChannelState?,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
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
        Spacer(modifier = Modifier.weight(1.0f))

        val color = if (state is ChannelState.Error) {
            Color.Red
        } else {
            AppColors.fontBlack
        }
        val stateDesc = when (state) {
            is ChannelState.Waiting -> "等待中"
            is ChannelState.Uploading -> "已上传${state.progress}%"
            is ChannelState.Success -> "上传成功"
            is ChannelState.Error -> "上传失败"
            else -> ""
        }
        Text(
            stateDesc,
            fontSize = 14.sp,
            color = color,
        )

    }
}


sealed class ChannelState {
    object Waiting : ChannelState()

    /**
     * @param progress 取值范围[0,100]
     */
    class Uploading(val progress: Int) : ChannelState()
    object Success : ChannelState()
    class Error(val message: String) : ChannelState()
}

//data class Channel(
//    /**
//     * 名称
//     */
//    val name: String,
//    /**
//     * 描述信息
//     */
//    val desc: String,
//    /**
//     * 状态
//     */
//    val state: ChannelState?,
//
//    /**
//     * 是否已选中
//     */
//    val selected: Boolean
//)
//
//fun Channel(name: String) = Channel(name, "", null, true)