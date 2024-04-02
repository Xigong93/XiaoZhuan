import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import widget.Section
import widget.TwoPage
import widget.UpdateDescView

class AndroidPage : Page("安卓页面") {

    private val updateDescView = UpdateDescView()

    private val selectedApkDir = mutableStateOf("/user/xigong/download/星题库/v5.30.0")

    private val channelGroupPage = ChannelGroupPage(
        listOf(
            Channel("华为", "", state = ChannelState.Waiting, true),
            Channel("小米", "", state = ChannelState.Waiting, true),
            Channel("OPPO", "", state = ChannelState.Waiting, true),
            Channel("VIVO", "", state = ChannelState.Waiting, true),
            Channel("荣耀", "", state = ChannelState.Waiting, true),
        )
    )

    @Composable
    override fun render() {
        TwoPage(
            leftPage = { LeftPage() },
            rightPage = { RightPage() },
        )
    }

    @Composable
    private fun ColumnScope.LeftPage() {
        val dividerHeight = 30.dp
        Section("操作") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = {}) {
                    Text("选择Apk文件夹", color = AppColors.fontGray)
                }
                Spacer(Modifier.width(10.dp))
                Text(selectedApkDir.value, color = AppColors.fontGray, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(dividerHeight))
        Section("版本信息") {
            VersionCodeBox()
        }
        Spacer(Modifier.height(dividerHeight))
        Section("更新描述") {
            updateDescView.render()
        }
    }


    @Composable
    private fun VersionCodeBox() {
        val textSize = 14.sp
        Column(
            modifier = Modifier.width(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row {
                Text(
                    "版本号:",
                    color = AppColors.fontGray,
                    fontSize = textSize,
                    modifier = Modifier.width(70.dp)
                )
                Text(
                    "v5.30.0",
                    color = AppColors.fontBlack,
                    fontSize = textSize
                )
            }
            Spacer(Modifier.height(12.dp))
            Row {
                Text(
                    "Apk大小:",
                    color = AppColors.fontGray,
                    fontSize = textSize,
                    modifier = Modifier.width(70.dp)
                )
                Text(
                    "75.6MB",
                    color = AppColors.fontBlack,
                    fontSize = textSize
                )
            }
        }
    }

    @Composable
    private fun ColumnScope.RightPage() {
        channelGroupPage.render()
    }


}



