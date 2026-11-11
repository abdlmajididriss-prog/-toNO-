package com.example.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TaskCategoryInfo(
    val name: String,
    val primaryColor: Color,
    val containerColorLight: Color,
    val onContainerColorLight: Color,
    val containerColorDark: Color,
    val onContainerColorDark: Color,
    val icon: ImageVector
)

object TaskCategoryUtils {
    val predefinedCategories = listOf(
        TaskCategoryInfo(
            name = "عمل",
            primaryColor = Color(0xFF2563EB), // Blue
            containerColorLight = Color(0xFFDBEAFE),
            onContainerColorLight = Color(0xFF1E40AF),
            containerColorDark = Color(0xFF1E3A8A),
            onContainerColorDark = Color(0xFFBFDBFE),
            icon = Icons.Default.Work
        ),
        TaskCategoryInfo(
            name = "شخصي",
            primaryColor = Color(0xFFD97706), // Amber
            containerColorLight = Color(0xFFFEF3C7),
            onContainerColorLight = Color(0xFF92400E),
            containerColorDark = Color(0xFF78350F),
            onContainerColorDark = Color(0xFFFDE68A),
            icon = Icons.Default.Person
        ),
        TaskCategoryInfo(
            name = "تسوق",
            primaryColor = Color(0xFF059669), // Emerald
            containerColorLight = Color(0xFFD1FAE5),
            onContainerColorLight = Color(0xFF065F46),
            containerColorDark = Color(0xFF064E3B),
            onContainerColorDark = Color(0xFFA7F3D0),
            icon = Icons.Default.ShoppingBag
        ),
        TaskCategoryInfo(
            name = "دراسة",
            primaryColor = Color(0xFF7C3AED), // Violet
            containerColorLight = Color(0xFFEDE9FE),
            onContainerColorLight = Color(0xFF5B21B6),
            containerColorDark = Color(0xFF4C1D95),
            onContainerColorDark = Color(0xFFDDD6FE),
            icon = Icons.Default.School
        ),
        TaskCategoryInfo(
            name = "صحة",
            primaryColor = Color(0xFFE11D48), // Rose
            containerColorLight = Color(0xFFFFE4E6),
            onContainerColorLight = Color(0xFF9F1239),
            containerColorDark = Color(0xFF881337),
            onContainerColorDark = Color(0xFFFECDD3),
            icon = Icons.Default.Favorite
        ),
        TaskCategoryInfo(
            name = "مالية",
            primaryColor = Color(0xFF0D9488), // Teal
            containerColorLight = Color(0xFFCCFBF1),
            onContainerColorLight = Color(0xFF115E59),
            containerColorDark = Color(0xFF134E4A),
            onContainerColorDark = Color(0xFF99F6E4),
            icon = Icons.Default.AccountBalanceWallet
        ),
        TaskCategoryInfo(
            name = "عائلي",
            primaryColor = Color(0xFF0284C7), // Sky Cyan
            containerColorLight = Color(0xFFE0F2FE),
            onContainerColorLight = Color(0xFF075985),
            containerColorDark = Color(0xFF0C4A6E),
            onContainerColorDark = Color(0xFFBAE6FD),
            icon = Icons.Default.Home
        ),
        TaskCategoryInfo(
            name = "عام",
            primaryColor = Color(0xFF64748B), // Slate Gray
            containerColorLight = Color(0xFFF1F5F9),
            onContainerColorLight = Color(0xFF334155),
            containerColorDark = Color(0xFF334155),
            onContainerColorDark = Color(0xFFE2E8F0),
            icon = Icons.Default.Label
        )
    )

    fun getCategoryInfo(name: String): TaskCategoryInfo {
        return predefinedCategories.firstOrNull { it.name.trim().equals(name.trim(), ignoreCase = true) }
            ?: TaskCategoryInfo(
                name = name.ifBlank { "عام" },
                primaryColor = Color(0xFF0F766E),
                containerColorLight = Color(0xFFCCFBF1),
                onContainerColorLight = Color(0xFF115E59),
                containerColorDark = Color(0xFF134E4A),
                onContainerColorDark = Color(0xFF99F6E4),
                icon = Icons.Default.Label
            )
    }
}
