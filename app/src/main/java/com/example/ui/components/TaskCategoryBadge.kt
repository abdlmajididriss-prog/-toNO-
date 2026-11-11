package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.util.TaskCategoryUtils

@Composable
fun TaskCategoryBadge(
    categoryName: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    showDot: Boolean = true
) {
    val isDark = isSystemInDarkTheme()
    val categoryInfo = TaskCategoryUtils.getCategoryInfo(categoryName)

    val bgColor = if (isDark) categoryInfo.containerColorDark.copy(alpha = 0.5f) else categoryInfo.containerColorLight
    val textColor = if (isDark) categoryInfo.onContainerColorDark else categoryInfo.onContainerColorLight
    val borderColor = categoryInfo.primaryColor.copy(alpha = if (isDark) 0.4f else 0.25f)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(categoryInfo.primaryColor)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }

            if (showIcon) {
                Icon(
                    imageVector = categoryInfo.icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = categoryInfo.name,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
