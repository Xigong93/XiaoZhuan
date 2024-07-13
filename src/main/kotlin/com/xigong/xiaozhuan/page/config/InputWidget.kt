package com.xigong.xiaozhuan.page.config

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xigong.xiaozhuan.channel.ChannelTask
import com.xigong.xiaozhuan.log.AppLogger
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import com.xigong.xiaozhuan.util.FileSelector
import com.xigong.xiaozhuan.widget.Section
import com.xigong.xiaozhuan.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 文本参数输入框
 */
@Composable
fun TextRaw(
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


/**
 * 文本文件参数输入框
 */
@Composable
fun TextFileRaw(
    name: String,
    desc: String,
    value: String?,
    textFile: ChannelTask.ParmaType.TextFile,
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
            onValueChange = { },
            textStyle = TextStyle(fontSize = textSize),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.fontGray,
                backgroundColor = Color.White
            ),
            maxLines = 4,
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        val scope = rememberCoroutineScope()
        Button(onClick = {
            scope.launch {
                selectedTextFile(textFile, onValueChange)
            }
        }, colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary)) {
            Text("选择文件", color = Color.White)
        }
    }
}

private suspend fun selectedTextFile(
    textFile: ChannelTask.ParmaType.TextFile,
    onValueChange: (String) -> Unit
) {
    val file = FileSelector.selectedFile(
        null,
        "*.${textFile.fileExtension}",
        listOf(textFile.fileExtension)
    )
    if (file != null) {
        try {
            val content = withContext(Dispatchers.IO) {
                file.readText()
            }
            check(content.isNotEmpty())
            onValueChange(content)
        } catch (e: Exception) {
            AppLogger.error("选择文件", "文件读取失败", e)
            Toast.show("文件读取失败")
        }
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
        TextRaw("AppKey", "", null, {})
        CheckboxRow(name = "急速模式", check = true) {}
    }
}

