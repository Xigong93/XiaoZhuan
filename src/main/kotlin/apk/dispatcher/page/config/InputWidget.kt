package apk.dispatcher.page.config

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apk.dispatcher.style.AppColors
import apk.dispatcher.style.AppShapes
import apk.dispatcher.widget.Section

/**
 * 参数输入框
 */
@Composable
fun InputRaw(
    name: String,
    desc: String,
    value: String?,
    onValueChange: (String) -> Unit
) {
    Section(name) {
        if (desc.isNotEmpty()) {
            Text(desc, color = AppColors.fontGray)
            Spacer(modifier = Modifier.height(10.dp))
        }
        val textSize = 14.sp
        OutlinedTextField(
            value = value ?: "",
            onValueChange = { onValueChange(it.trim()) },
            textStyle = TextStyle(fontSize = textSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.primary,
                backgroundColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

}


@Composable
fun CheckboxRow(modifier: Modifier = Modifier, name: String, check: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier
        .clip(AppShapes.roundButton)
        .fillMaxWidth()
        .clickable {
            onCheckedChange(check.not())
        }) {
        Checkbox(
            check,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
        )
        Text(name)
    }
}

@Preview
@Composable
private fun ParamInputPreview() {
    Column {
        InputRaw("AppKey", "", null, {})
        CheckboxRow(name = "急速模式", check = true) {}
    }
}

