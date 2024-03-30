import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import widget.Section
import widget.TwoPage
import widget.UpdateDescView
import widget.UpdateTypeView

class ApplePage : Page("苹果页面") {

    private val updateTypeView = UpdateTypeView()

    private val versionCode = mutableStateOf("")

    private val updateDescView = UpdateDescView()

    private val channelGroupPage = ChannelGroupPage(
        listOf(Channel("应用内", "", state = ChannelState.Waiting, true))
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
        Section("更新类型") {
            updateTypeView.render()
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
        OutlinedTextField(
            value = versionCode.value,
            placeholder = {
                Text(
                    "请输入版本号如5.30.1",
                    color = AppColors.fontGray,
                    fontSize = textSize
                )
            },
            maxLines = 1,
            onValueChange = { versionCode.value = it },
            textStyle = TextStyle(fontSize = textSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.primary,
                backgroundColor = Color.White
            ),
            modifier = Modifier
                .width(300.dp)
        )
    }

    @Composable
    private fun ColumnScope.RightPage() {
        channelGroupPage.render()
    }

}