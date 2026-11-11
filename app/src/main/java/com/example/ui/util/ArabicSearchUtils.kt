package com.example.ui.util

object ArabicSearchUtils {

    /**
     * Normalizes Arabic text by removing tashkeel diacritics and unifying
     * variants of Alif (أ, إ, آ, ٱ -> ا), Taa Marbuta (ة -> ه), and Yaa/Alef Maksura (ى -> ي).
     */
    fun normalizeArabic(text: String): String {
        return text
            .replace(Regex("[\\u064B-\\u065F\\u0670]"), "") // remove diacritics (harakat)
            .replace(Regex("[أإآٱ]"), "ا")
            .replace('ة', 'ه')
            .replace('ى', 'ي')
            .replace('ؤ', 'و')
            .replace('ئ', 'ي')
            .trim()
            .lowercase()
    }

    /**
     * Checks if the target source contains all space-separated keywords from the query,
     * utilizing normalized Arabic comparison.
     */
    fun matchesQuery(source: String, query: String): Boolean {
        if (query.isBlank()) return true
        val normalizedSource = normalizeArabic(source)
        val normalizedQuery = normalizeArabic(query)

        val keywords = normalizedQuery.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (keywords.isEmpty()) return true

        return keywords.all { normalizedSource.contains(it) }
    }

    /**
     * Checks if either title, description/content or category matches the query.
     */
    fun matchesMultiple(query: String, vararg fields: String): Boolean {
        if (query.isBlank()) return true
        val combined = fields.joinToString(" ")
        return matchesQuery(combined, query)
    }
}
