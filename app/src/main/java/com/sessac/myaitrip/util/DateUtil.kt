package com.sessac.myaitrip.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateUtil {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    fun getCurrentDate(): String{
        return LocalDateTime.now().format(dateFormatter)
    }

    fun getYesterdayDate(): String{
        return LocalDateTime.now().minusDays(1).format(dateFormatter)
    }

    fun getCurrentHour(): Int{
        return LocalTime.now().hour // 0에서 23 사이의 값을 반환
    }

    fun getCurrentHourWithFormatted(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH'00'")
        return currentTime.format(formatter)
    }
}