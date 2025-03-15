package com.xigong.xiaozhuan.page.home

import java.util.*

/**
 * 发布时间
 */
data class ReleaseDate(
    /**
     * 取值范围[2025,...]
     */
    val year: Int,
    /**
     * 取值范围
     * [1,12]
     */
    val month: Int,
    /**
     * 取值范围
     * [1,31]
     */
    val day: Int,
    /**
     * 取值范围[0,23]
     */
    val hour: Int
) {
    companion object {
        fun default(): ReleaseDate {
            // 默认设置的3天后的上午10点钟
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 10)
            calendar.add(Calendar.DAY_OF_YEAR, 3)
            return fromDate(calendar.time)
        }

        fun fromDate(date: Date): ReleaseDate {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return ReleaseDate(
                year = calendar.get(Calendar.YEAR),
                month = (calendar.get(Calendar.MONTH) + 1),
                day = calendar.get(Calendar.DAY_OF_MONTH),
                hour = calendar.get(Calendar.HOUR_OF_DAY),
            )
        }
    }


    /**
     * 转成日期
     */
    fun toDate(): Date {
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