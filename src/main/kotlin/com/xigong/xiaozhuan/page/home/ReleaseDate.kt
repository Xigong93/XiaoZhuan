package com.xigong.xiaozhuan.page.home

import java.util.*

/**
 * 发布时间
 */
data class ReleaseDate(
    val year: String,
    val month: String,
    val day: String,
    val hour: String
) {
    companion object {
        fun new(): ReleaseDate {
            val calendar = Calendar.getInstance()
            // 默认设置的3天后的上午10点钟表
            calendar.set(Calendar.HOUR_OF_DAY, 10)
            calendar.add(Calendar.DAY_OF_YEAR, 3)
            return ReleaseDate(
                year = calendar.get(Calendar.YEAR).toString(),
                month = (calendar.get(Calendar.MONTH) + 1).toString(),
                day = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                hour = calendar.get(Calendar.HOUR_OF_DAY).toString(),
            )
        }
    }


    /**
     * 转成日期
     */
    fun getData(): Date {
        val year = year.toIntOrNull() ?: 0
        val month = month.toIntOrNull() ?: 0
        val day = day.toIntOrNull() ?: 0
        val hour = hour.toIntOrNull() ?: 0
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }


}