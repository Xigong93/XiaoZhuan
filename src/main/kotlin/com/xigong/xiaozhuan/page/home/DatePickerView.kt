package com.xigong.xiaozhuan.page.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt




/**
 * 日期数据源
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @param selectedTime 默认选中的时间
 */
class DatePickerController(
    private val startTime: Date,
    private val endTime: Date,
    private val selectedTime: Date
) {


    val selectedYearIndex: MutableState<Int> = mutableStateOf(0)
    val selectedMonthIndex: MutableState<Int> = mutableStateOf(selectedTime.month)
    val selectedDayIndex: MutableState<Int> = mutableStateOf(selectedTime.date - 1)
    val selectedHourIndex: MutableState<Int> = mutableStateOf(selectedTime.hours)

    val years: List<String> = (startTime.year..endTime.year).toList().map { "${it + 1900}年" }

    val months: List<String> = (1..12).toList().map { "${it}月" }

    val days: List<String> by derivedStateOf {
        (1..getMonthDayCount()).toList().map { "${it}日" }
    }
    val hours: List<String> = (0..23).toList().map { "${it}时" }

    /**
     * 选中的日期
     */
    val selectedDate: Date by derivedStateOf {
        val year = selectedYearIndex.value + startTime.year + 1900
        val month = selectedMonthIndex.value
        val day = selectedDayIndex.value + 1
        val hour = selectedHourIndex.value
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.time
    }

    init {
        if (startTime.after(endTime)) {
            throw IllegalArgumentException("日期参数不正确，endTime 小于 startTime")
        }
    }

    /**
     * 获取某年某月有几天
     */
    private fun getMonthDayCount(): Int {
        val year = selectedYearIndex.value + 1900
        val month = selectedMonthIndex.value
        return YearMonth.of(year, month + 1).lengthOfMonth()
    }


    companion object {
        /**
         * 开始时间默认是明天,
         * 结束时间默认是下个月
         * 选中时间默认是三天后的0点
         */
        fun default(): DatePickerController {
            val startTime = Date()

            val endTime = {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, 1)
                calendar.time

            }
            val selectedTime = {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, 3)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.time
            }
            return DatePickerController(startTime, endTime(), selectedTime())
        }
    }

}

@Preview
@Composable
fun DatePickerView(dateSource: DatePickerController) {
    Column(
        Modifier

    ) {
        Box(Modifier.height(320.dp).width(IntrinsicSize.Min)) {

            Spacer(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .height(40.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .background(Color(0xff297be8))
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Selection(Modifier.width(80.dp), dateSource.years, dateSource.selectedYearIndex.value) { index ->
                    dateSource.selectedYearIndex.value = index
                }
                Selection(Modifier.width(80.dp), dateSource.months, dateSource.selectedMonthIndex.value) { index ->
                    dateSource.selectedMonthIndex.value = index
                }
                Selection(Modifier.width(80.dp), dateSource.days, dateSource.selectedDayIndex.value) { index ->
                    dateSource.selectedDayIndex.value = index
                }
                Selection(Modifier.width(80.dp), dateSource.hours, dateSource.selectedHourIndex.value) { index ->
                    dateSource.selectedHourIndex.value = index
                }
            }
        }

    }

}


private class PageController(
    private val pageHeight: Int,
    private val scrollState: ScrollState,
    private val scope: CoroutineScope,
    private val defaultSelectedIndex: Int,
    private val onSelected: (index: Int) -> Unit
) {


    val currentPage by derivedStateOf {
        (scrollState.value.toFloat() / pageHeight).roundToInt()
    }

    init {
        scope.launch {
            scrollState.scrollTo(defaultSelectedIndex * pageHeight)
            snapshotFlow { currentPage }
                .collect { page ->
                    println("当前选中第${page}页")
                    onSelected(page)
                }
        }
    }

    private var pendingDistance = 0

    private var animScrolling = false

    private var flipJob: Job? = null

    fun flip() {
        if (animScrolling) {
            return
        }
        flipJob = scope.launch {
            delay(300)
            val offset = currentPage * pageHeight
            println("flip:滚动到，第${currentPage}页,偏移量:${offset},scrolling:${scrollState.isScrollInProgress}")
            animateScrollTo(offset)
        }
    }

    fun scrollUp() {
        pendingDistance -= pageHeight
        calcRange()
        scrollToPend()
    }

    fun scrollDown() {
        pendingDistance += pageHeight
        calcRange()
        scrollToPend()
    }

    private fun calcRange() {
        pendingDistance = pendingDistance.coerceIn(0 - scrollState.value, scrollState.maxValue - scrollState.value)
    }

    private var pendingJob: Job? = null
    private fun scrollToPend() {
        flipJob?.cancel()
        pendingJob?.cancel()
        pendingJob = scope.launch {
            val startPosition = scrollState.value
            val position = (startPosition + pendingDistance).coerceIn(0, scrollState.maxValue)
            try {
                animateScrollTo(position)
            } finally {
                val diff = scrollState.value - startPosition
                pendingDistance -= diff
            }
        }


    }

    private suspend fun animateScrollTo(offset: Int) {
        try {
            animScrolling = true
            val page = (offset / pageHeight.toFloat()).roundToInt()
            val fixedOffset = page * pageHeight
            println("滚动到，第${page}页,偏移量:${fixedOffset}")
            scrollState.animateScrollTo(fixedOffset)
        } finally {
            animScrolling = false
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Selection(
    modifier: Modifier,
    items: List<String>,
    defaultSelectedIndex: Int,
    onSelected: (index: Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered = interactionSource.collectIsHoveredAsState()
    Box(
        modifier = modifier
            .hoverable(interactionSource)
    ) {
        val scrollState = rememberScrollState()
        val pageHeight = with(LocalDensity.current) { 40.dp.toPx() }
        val scope = rememberCoroutineScope()
        val pageController = remember {
            PageController(pageHeight.toInt(), scrollState, scope, defaultSelectedIndex, onSelected)
        }


        // 监听滑动
        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.value }
                .collect { pageController.flip() }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)
                .onPointerEvent(PointerEventType.Scroll) { pointerEvent ->
                    val changes = pointerEvent.changes
                    val y = changes.first().scrollDelta.y.toInt()
                    if (y.absoluteValue > 0) {
                        changes.forEach { it.consume() }
                        if (y > 0) {
                            pageController.scrollDown()
                        } else {
                            pageController.scrollUp()
                        }
                    }
                }
                .fillMaxSize(),
        ) {
            Spacer(Modifier.height(140.dp))
            items.forEachIndexed { index, item ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                        .height(40.dp),
                ) {
                    val fontColor = if (pageController.currentPage == index) Color.White else Color.Black
                    Text(item, fontSize = 16.sp, color = fontColor, modifier = Modifier)
                }

            }
            Spacer(Modifier.height(140.dp))
        }
        if (hovered.value) {
            ActionButtons(onUpClick = {
                pageController.scrollUp()
            }, onDownClick = {
                pageController.scrollDown()
            })
        }

    }

}

@Composable
private fun BoxScope.ActionButtons(onUpClick: () -> Unit, onDownClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .align(Alignment.TopCenter)
            .background(Color.White)
            .clickable {
                onUpClick()
            }
            .padding(vertical = 10.dp)

    ) {
        Image(
            painterResource("date_picker_arrow.png"),
            contentDescription = "UP",
            modifier = Modifier.size(width = 16.dp, height = 12.dp)
        )
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .align(Alignment.BottomCenter)
            .background(Color.White)
            .clickable {
                onDownClick()
            }
            .padding(vertical = 10.dp)

    ) {
        Image(
            painterResource("date_picker_arrow.png"),
            contentDescription = "Down",
            modifier = Modifier.size(width = 16.dp, height = 12.dp)
                .rotate(180f)

        )
    }
}



