package com.example

import com.example.ui.util.ArabicSearchUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testArabicNormalization() {
        // Tashkeel removal and hamza normalization
        val normalized = ArabicSearchUtils.normalizeArabic("مُراجَعَةُ الخُطَّةِ الأُسْبُوعِيَّة")
        assertEquals("مراجعه الخطه الاسبوعيه", normalized)
    }

    @Test
    fun testSearchKeywordMatching() {
        val taskTitle = "مراجعة تقرير خطة العمل الأسبوعية"
        val taskDesc = "إعداد الملخص للمشاريع الحالية وتحديد الأولويات"

        // Search with different hamza variations
        assertTrue(ArabicSearchUtils.matchesMultiple("تقرير", taskTitle, taskDesc))
        assertTrue(ArabicSearchUtils.matchesMultiple("اعداد", taskTitle, taskDesc))
        assertTrue(ArabicSearchUtils.matchesMultiple("الاسبوعيه", taskTitle, taskDesc))

        // Multiple keywords
        assertTrue(ArabicSearchUtils.matchesMultiple("تقرير عمل", taskTitle, taskDesc))
        assertTrue(ArabicSearchUtils.matchesMultiple("مراجعة مشاريع", taskTitle, taskDesc))

        // Non-matching query
        assertFalse(ArabicSearchUtils.matchesMultiple("سيارة", taskTitle, taskDesc))
    }

    @Test
    fun testNoteContentMatching() {
        val noteTitle = "أهداف الشهر الجديد"
        val noteContent = "1. تحسين الروتين الصباحي.\n2. إدخار 25% من الدخل في حساب الطوارئ."

        assertTrue(ArabicSearchUtils.matchesMultiple("اهداف", noteTitle, noteContent))
        assertTrue(ArabicSearchUtils.matchesMultiple("ادخار", noteTitle, noteContent))
        assertTrue(ArabicSearchUtils.matchesMultiple("روتين صباحي", noteTitle, noteContent))
        assertFalse(ArabicSearchUtils.matchesMultiple("سفر طيران", noteTitle, noteContent))
    }

    @Test
    fun testTaskCategoryColorCoding() {
        val workCategory = com.example.ui.util.TaskCategoryUtils.getCategoryInfo("عمل")
        assertEquals("عمل", workCategory.name)

        val shoppingCategory = com.example.ui.util.TaskCategoryUtils.getCategoryInfo("تسوق")
        assertEquals("تسوق", shoppingCategory.name)

        val personalCategory = com.example.ui.util.TaskCategoryUtils.getCategoryInfo("شخصي")
        assertEquals("شخصي", personalCategory.name)

        // Custom unknown category falls back gracefully
        val customCategory = com.example.ui.util.TaskCategoryUtils.getCategoryInfo("مشروع خاص")
        assertEquals("مشروع خاص", customCategory.name)
    }

    @Test
    fun testTaskReminderTriggerCalculation() {
        val exactMillis = com.example.service.TaskReminderScheduler.calculateTriggerMillis("2026-11-15", "10:00", 0)
        org.junit.Assert.assertNotNull(exactMillis)

        val before15Millis = com.example.service.TaskReminderScheduler.calculateTriggerMillis("2026-11-15", "10:00", 15)
        org.junit.Assert.assertNotNull(before15Millis)

        // 15 minutes before should be exactly 15 * 60 * 1000 millis less
        assertEquals(15 * 60 * 1000L, exactMillis!! - before15Millis!!)
    }

    @Test
    fun testTaskCategoryDistributionCalculation() {
        val tasks = listOf(
            com.example.data.local.entities.TaskEntity(title = "مهمة 1", category = "عمل"),
            com.example.data.local.entities.TaskEntity(title = "مهمة 2", category = "عمل"),
            com.example.data.local.entities.TaskEntity(title = "مهمة 3", category = "شخصي"),
            com.example.data.local.entities.TaskEntity(title = "مهمة 4", category = "تسوق")
        )

        val total = tasks.size
        assertEquals(4, total)

        val grouped = tasks.groupBy { it.category }
        assertEquals(2, grouped["عمل"]?.size)
        assertEquals(1, grouped["شخصي"]?.size)
        assertEquals(1, grouped["تسوق"]?.size)

        val workPercent = (grouped["عمل"]!!.size.toFloat() / total) * 100f
        assertEquals(50f, workPercent, 0.01f)

        val personalPercent = (grouped["شخصي"]!!.size.toFloat() / total) * 100f
        assertEquals(25f, personalPercent, 0.01f)

        val shoppingPercent = (grouped["تسوق"]!!.size.toFloat() / total) * 100f
        assertEquals(25f, shoppingPercent, 0.01f)
    }
}

