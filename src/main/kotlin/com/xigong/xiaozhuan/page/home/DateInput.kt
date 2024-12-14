package com.xigong.xiaozhuan.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes

/**
 * 日期输出框，可以输入年月日和几点
 */
@Composable
fun DateInput(date: ReleaseDate, onDateChange: (ReleaseDate) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
        val fontSize = 12.sp
        val gap = Modifier.padding(horizontal = 10.dp)

        // 年
        DateInputCell(date.year, 60.dp, onValueChange = {
            onDateChange(date.copy(year = it))
        })
        Text("年", fontSize = fontSize, modifier = gap)

        // 月
        DateInputCell(date.month, 40.dp, onValueChange = {
            onDateChange(date.copy(month = it))

        })
        Text("月", fontSize = fontSize, modifier = gap)

        // 日
        DateInputCell(date.day, 40.dp, onValueChange = {
            onDateChange(date.copy(day = it))

        })
        Text("日", fontSize = fontSize, modifier = gap)

        // 时
        DateInputCell(date.hour, 40.dp, onValueChange = {
            onDateChange(date.copy(hour = it))
        })
        Text("时发布", fontSize = fontSize, modifier = gap)
        Text("(24时制)", fontSize = fontSize, color = AppColors.fontGray)
    }
}

@Composable
private fun DateInputCell(number: String, width: Dp, onValueChange: (String) -> Unit) {


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .border(1.dp, AppColors.fontGray, AppShapes.roundButton)
    ) {
        BasicTextField(
            number,
            textStyle = TextStyle(fontSize = 12.sp),
            singleLine = true,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(width)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

    }

}

@Preview
@Composable
fun DateInputPreview() {
    var date by remember { mutableStateOf(ReleaseDate.new()) }
    DateInput(date) {
        date = it
    }
}