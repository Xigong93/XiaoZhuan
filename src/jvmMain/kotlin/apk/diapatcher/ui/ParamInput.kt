package apk.diapatcher.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.diapatcher.ApkChannel
import apk.diapatcher.style.AppColors
import apk.diapatcher.widget.Section

class ParamInput(private val param: ApkChannel.Param, value: String?) {
    private val input = mutableStateOf(value ?: param.defaultValue ?: "")

    @Composable
    fun render() {
        val textSize = 14.sp
        Section(param.name) {
            val desc = param.desc ?: ""
            if (desc.isNotEmpty()) {
                Text(desc, color = AppColors.fontGray)
                Spacer(modifier = Modifier.height(10.dp))
            }
            OutlinedTextField(
                value = input.value,
                onValueChange = { input.value = it },
                textStyle = TextStyle(fontSize = textSize),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppColors.primary,
                    backgroundColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun ParamInputPreview() {
    ParamInput(param = ApkChannel.Param("ApiKey", null, "ApiKey"), null).render()
}