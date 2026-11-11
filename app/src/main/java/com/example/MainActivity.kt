package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.NoteDialog
import com.example.ui.components.PlannerEventDialog
import com.example.ui.components.TaskDialog
import com.example.ui.components.TransactionDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.OrganizerViewModel
import com.example.ui.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val organizerViewModel: OrganizerViewModel = viewModel()
            val themeMode by organizerViewModel.themeMode.collectAsState()

            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val context = LocalContext.current
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // Arabic RTL layout direction provider
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme(darkTheme = isDark) {
                    OrganizerApp(viewModel = organizerViewModel, currentThemeMode = themeMode)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerApp(
    viewModel: OrganizerViewModel,
    currentThemeMode: ThemeMode
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Handle back button: return to Dashboard if on any other tab
    BackHandler(enabled = selectedTab != AppTab.DASHBOARD) {
        viewModel.selectTab(AppTab.DASHBOARD)
    }

    // Dialog state collections
    val showTaskDialog by viewModel.showTaskDialog.collectAsState()
    val taskToEdit by viewModel.taskToEdit.collectAsState()

    val showNoteDialog by viewModel.showNoteDialog.collectAsState()
    val noteToEdit by viewModel.noteToEdit.collectAsState()

    val showTransactionDialog by viewModel.showTransactionDialog.collectAsState()
    val transactionToEdit by viewModel.transactionToEdit.collectAsState()

    val showEventDialog by viewModel.showEventDialog.collectAsState()
    val eventToEdit by viewModel.eventToEdit.collectAsState()

    val isGlobalSearchOpen by viewModel.isGlobalSearchOpen.collectAsState()
    val deleteTarget by viewModel.deleteTarget.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "شعار منظمي الشخصي",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .testTag("appbar_logo_icon")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (selectedTab) {
                                AppTab.DASHBOARD -> "منظمي الشخصي"
                                AppTab.TASKS -> "إدارة المهام"
                                AppTab.PLANNER -> "المخطط والتقويم"
                                AppTab.FINANCE -> "الميزانية والمصروفات"
                                AppTab.NOTES -> "المفكرة والملاحظات"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openGlobalSearch() },
                        modifier = Modifier.testTag("appbar_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "البحث في المهام والملاحظات"
                        )
                    }

                    IconButton(
                        onClick = {
                            val nextTheme = when (currentThemeMode) {
                                ThemeMode.SYSTEM -> ThemeMode.DARK
                                ThemeMode.DARK -> ThemeMode.LIGHT
                                ThemeMode.LIGHT -> ThemeMode.SYSTEM
                            }
                            viewModel.setThemeMode(nextTheme)
                        },
                        modifier = Modifier.testTag("appbar_theme_toggle")
                    ) {
                        Icon(
                            imageVector = when (currentThemeMode) {
                                ThemeMode.DARK -> Icons.Default.NightlightRound
                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            },
                            contentDescription = "تبديل وضع المظهر (${currentThemeMode.title})"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                listOf(
                    NavigationItemData(AppTab.DASHBOARD, Icons.Default.Dashboard, "الرئيسة"),
                    NavigationItemData(AppTab.TASKS, Icons.Default.CheckCircle, "المهام"),
                    NavigationItemData(AppTab.PLANNER, Icons.Default.CalendarMonth, "التقويم"),
                    NavigationItemData(AppTab.FINANCE, Icons.Default.AccountBalanceWallet, "الميزانية"),
                    NavigationItemData(AppTab.NOTES, Icons.Default.Description, "الملاحظات")
                ).forEach { item ->
                    val isSelected = selectedTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${item.tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppTab.TASKS -> TasksScreen(viewModel = viewModel)
                AppTab.PLANNER -> PlannerScreen(viewModel = viewModel)
                AppTab.FINANCE -> FinanceScreen(viewModel = viewModel)
                AppTab.NOTES -> NotesScreen(viewModel = viewModel)
            }
        }

        // --- Dialog Overlays ---
        if (showTaskDialog && taskToEdit != null) {
            TaskDialog(
                task = taskToEdit!!,
                onDismiss = { viewModel.dismissTaskDialog() },
                onSave = { viewModel.saveTask(it) }
            )
        }

        if (showNoteDialog && noteToEdit != null) {
            NoteDialog(
                note = noteToEdit!!,
                onDismiss = { viewModel.dismissNoteDialog() },
                onSave = { viewModel.saveNote(it) }
            )
        }

        if (showTransactionDialog && transactionToEdit != null) {
            TransactionDialog(
                transaction = transactionToEdit!!,
                onDismiss = { viewModel.dismissTransactionDialog() },
                onSave = { viewModel.saveTransaction(it) }
            )
        }

        if (showEventDialog && eventToEdit != null) {
            PlannerEventDialog(
                event = eventToEdit!!,
                onDismiss = { viewModel.dismissEventDialog() },
                onSave = { viewModel.saveEvent(it) }
            )
        }

        if (deleteTarget != null) {
            DeleteConfirmDialog(
                target = deleteTarget!!,
                onDismiss = { viewModel.dismissDeleteConfirm() },
                onConfirm = { viewModel.executeDelete() }
            )
        }

        if (isGlobalSearchOpen) {
            com.example.ui.components.GlobalSearchDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.closeGlobalSearch() }
            )
        }
    }
}

private data class NavigationItemData(
    val tab: AppTab,
    val icon: ImageVector,
    val label: String
)
