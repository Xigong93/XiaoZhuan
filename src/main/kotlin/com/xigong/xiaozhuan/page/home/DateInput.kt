package com.xigong.xiaozhuan.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xigong.xiaozhuan.style.AppColors
import com.xigong.xiaozhuan.style.AppShapes
import com.xigong.xiaozhuan.widget.Toast
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


/**
 * 日期输出框，可以输入年月日和几点
 */
@Composable
fun BoxScope.DateInput(date: ReleaseDate, onDateChange: (ReleaseDate) -> Unit) {
    val showDatePicker = remember { mutableStateOf(false) }
    if (showDatePicker.value) {
        DatePickerDialog("选择发布时间", date.toDate(), onDismiss = {
            showDatePicker.value = false
        }, onDateSelected = { selectedDate ->
            if (selectedDate.before(Date())) {
                Toast.show("不能早于当前时间")
            } else if (isMoreThanOneMonth(Date(), selectedDate)) {
                Toast.show("仅支持选择1个月内的时间")
            } else {
                onDateChange(ReleaseDate.fromDate(selectedDate))
                showDatePicker.value = false
            }
        })
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(IntrinsicSize.Min)
            .align(Alignment.Center)
            .clickable(indication = null, interactionSource = null) {
                showDatePicker.value = true
            }
    ) {
        val fontSize = 14.sp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .border(1.dp, AppColors.fontGray, AppShapes.roundButton)
                .padding(horizontal = 20.dp)
        ) {
            val dateStr = "${date.year}年${date.month}月${date.day}日 ${date.hour}时"
            Text(
                text = dateStr,
                fontSize = fontSize,
                color = AppColors.fontBlack,
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)

            )

        }
    }

}


/**
 * 判断两个Date是否相差超过一个月
 * @param date1 第一个日期
 * @param date2 第二个日期
 * @return 如果date2在date1之后超过一个月，返回true；否则返回false
 */
fun isMoreThanOneMonth(date1: Date, date2: Date): Boolean {
    // 转换为LocalDate并截取时间部分（默认时区）
    val localDate1: LocalDate = date1.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val localDate2: LocalDate = date2.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    // 计算第一个日期下个月的同一天
    val nextMonthDate = localDate1.plusMonths(1)

    // 判断第二个日期是否在下个月之后
    return localDate2.isAfter(nextMonthDate)
}

