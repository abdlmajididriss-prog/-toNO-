package com.example.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ArabicDateUtils {

    val arabicDays = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")

    val arabicMonths = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
    }

    fun getCurrentMonthPrefix(): String {
        return SimpleDateFormat("yyyy-MM", Locale.ENGLISH).format(Date())
    }

    fun formatToFullArabicDate(dateString: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val date = format.parse(dateString) ?: return dateString
            val cal = Calendar.getInstance().apply { time = date }

            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
            val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH) // 0-based
            val year = cal.get(Calendar.YEAR)

            val dayName = arabicDays[(dayOfWeek - 1) % 7]
            val monthName = arabicMonths.getOrElse(month) { "" }

            "$dayName، $dayOfMonth $monthName $year"
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatToArabicMonthYear(monthPrefix: String): String {
        return try {
            val parts = monthPrefix.split("-")
            if (parts.size == 2) {
                val year = parts[0]
                val monthIdx = parts[1].toInt() - 1
                val monthName = arabicMonths.getOrElse(monthIdx) { parts[1] }
                "$monthName $year"
            } else {
                monthPrefix
            }
        } catch (e: Exception) {
            monthPrefix
        }
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> "صباح الخير والبركة ☀️"
            in 12..16 -> "طاب يومك بكل خير 🌿"
            in 17..22 -> "مساء الخير والسكينة 🌙"
            else -> "ليلة هانئة وسعيدة ✨"
        }
    }

    fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "%,.1f", amount)
    }
}
