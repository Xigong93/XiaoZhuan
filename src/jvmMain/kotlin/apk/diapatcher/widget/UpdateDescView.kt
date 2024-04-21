package apk.diapatcher.widget

import apk.diapatcher.style.AppColors
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class UpdateDescView() {
    private val updateDesc = mutableStateOf("")

    @Composable
    fun render() {
        val textSize = 14.sp
        OutlinedTextField(
            value = updateDesc.value,
            placeholder = {
                Text(
                    "请填写更新描述",
                    color = AppColors.fontGray,
                    fontSize = textSize
                )
            },
            onValueChange = { updateDesc.value = it },
            textStyle = TextStyle(fontSize = textSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.primary,
                backgroundColor = Color.White
            ),
            modifier = Modifier
                .width(300.dp)
                .height(120.dp)
        )
    }
}