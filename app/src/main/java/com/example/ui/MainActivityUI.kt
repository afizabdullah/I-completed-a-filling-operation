package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.service.FloatingOverlayService
import com.example.service.WebShieldAccessibilityService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// Translation Strings Registry (Arabic RTL & English LTR Support)
object Locales {
    val AR = mapOf(
        "app_title" to "ويب شيلد برو",
        "service_status" to "حالة الخدمة والحماية",
        "active" to "نشط ومؤمن",
        "inactive" to "متوقف مؤقتاً",
        "threats_filtered" to "تصفية 1,248 برمجية تهديد نشطة",
        "safe_shield" to "جدار حماية ذكي نشط",
        
        "tab_dashboard" to "الرئيسية",
        "tab_quick_data" to "البيانات السريعة",
        "tab_autofill" to "التعبئة التلقائية",
        "tab_ai" to "المساعد الذكي",
        "tab_more" to "المزيد",

        "tab_stats" to "الإحصائيات",
        "tab_logs" to "السجلات",
        "tab_libs" to "المكتبات",
        "tab_vault" to "الخزنة",
        "tab_settings" to "الإعدادات",
        "tab_about" to "حول التطبيق",
        "tab_activation" to "التفعيل",
        
        "stats_title" to "لوحة التصفية ومنع الاختراقات",
        "stats_adblock" to "مانع الإعلانات",
        "stats_popups" to "النوافذ المنبثقة",
        "stats_cookies" to "كوكيز التتبع",
        "stats_threats" to "التهديدات الممنوعة",
        
        "logs_title" to "سجلات المراقبة المباشرة",
        "logs_clear" to "مسح السجلات",
        "logs_empty" to "لا توجد سجلات حماية مخزنة حالياً.",
        "log_level" to "المستوى",
        "log_tag" to "الوحدة",
        
        "libs_title" to "إدارة قواعد نصوص الحماية",
        "libs_desc" to "قائمة حزم الحماية والمكتبات البرمجية المفعلة لفحص التهديدات وعزل الأكواد الخبيثة.",
        "libs_add" to "إضافة حزمة جديدة",
        "lib_name" to "اسم حزمة القواعد",
        "lib_version" to "الإصدار",
        
        "vault_title" to "خزنة الروابط والملاحظات الملقطة",
        "vault_desc" to "أرشيف عناوين الويب ومقالات التصفح والملاحظات السريعة.",
        "add_clip" to "حفظ رابط جديد",
        "add_note" to "ملاحظة سريعة جديدة",
        
        "activation_title" to "تفعيل ترخيص WebShield VIP",
        "activation_desc" to "أدخل الرمز الترقوي المعتمد لترقية جدار الحماية وفتح الأيقونة العائمة للأمان بدون حدود.",
        "activate" to "تفعيل الآن",
        "activation_code" to "رمز ترخيص المنتج",
        "active_vip" to "عضوية VIP مفعلة مدى الحياة ✨",
        "invalid_code" to "رمز التفعيل غير صالح. يرجى تجربة رمز صحيح مثل: SHIELD-2026-PRO",
        "success_code" to "تم التفعيل بنجاح! شكراً لثقتك بـ WebShield Pro.",
        
        "settings_title" to "تعديل المظهر والتفضيلات",
        "settings_lang" to "لغة واجهة التطبيق",
        "settings_theme" to "سمة المظهر العام",
        "settings_light" to "الوضع المضيء المريح",
        "settings_dark" to "المظهر الداكن الفاخر",
        "settings_overlay" to "إعدادات الأيقونة العائمة فوق الشاشة",
        "settings_overlay_desc" to "مساعد ذكي يعمل كخدمة مستمرة (Foreground Service) لسحب وتخزين الروابط.",
        "settings_overlay_status" to "صلاحية الظهور فوق التطبيقات",
        "request_overlay" to "منح صلاحية الظهور",
        "start_service" to "تشغيل الخدمة العائمة",
        "stop_service" to "إيقاف الخدمة العائمة",
        "about_title" to "حول WebShield Pro",
        "about_desc" to "تطبيق ويب شيلد برو هو جدار أمان متكامل وأداة clipping ذكية لمكافحة الإعلانات وتصفية محتوى المتصفح بشكل كامل محلياً وبكل خصوصية.",
        "version" to "الإصدار: Pro v4.2.1",
        "rights" to "جميع الحقوق محفوظة © 2026"
    )

    val EN = mapOf(
        "app_title" to "WebShield Pro",
        "service_status" to "Service Status & Guard",
        "active" to "Active & Secure",
        "inactive" to "Temporarily Suspended",
        "threats_filtered" to "Filtering 1,248 active threats",
        "safe_shield" to "Active Secured Firewall Enabled",
        
        "tab_dashboard" to "Dashboard",
        "tab_quick_data" to "Quick Data",
        "tab_autofill" to "Autofill",
        "tab_ai" to "AI Assistant",
        "tab_more" to "More",

        "tab_stats" to "Stats",
        "tab_logs" to "Watchdog Logs",
        "tab_libs" to "Script Shields",
        "tab_vault" to "Vault Archive",
        "tab_settings" to "Settings",
        "tab_about" to "About Pro",
        "tab_activation" to "Licenses",
        
        "stats_title" to "Network Filter & Block Panel",
        "stats_adblock" to "Ad Blocker",
        "stats_popups" to "Popup Blocker",
        "stats_cookies" to "Tracker Cookies",
        "stats_threats" to "Blocked Threats",
        
        "logs_title" to "Live Firewall Watchdog Logs",
        "logs_clear" to "Clear Database Logs",
        "logs_empty" to "No live logs saved currently.",
        "log_level" to "Level",
        "log_tag" to "Module",
        
        "libs_title" to "Shield Script & Rule Packages",
        "libs_desc" to "Active extension security rulesets loaded to identify malicious scripts and track ad requests.",
        "libs_add" to "Inject Rule Package",
        "lib_name" to "Package Name",
        "lib_version" to "Version",
        
        "vault_title" to "Saved Clips & Offline Notepad",
        "vault_desc" to "Encrypted vault stores Web Clip URLs and quick diary note frames securely.",
        "add_clip" to "New URL Snippet",
        "add_note" to "Write Notepad",
        
        "activation_title" to "Activate WebShield VIP Premium",
        "activation_desc" to "Provide the registered commercial licensing code to activate high performance firewall filters and floating overlay clipper components.",
        "activate" to "Activate Forever",
        "activation_code" to "Device Licensing Key",
        "active_vip" to "VIP Premium License Lifetime Enabled ✨",
        "invalid_code" to "Invalid subscription key. Use mock code: SHIELD-2026-PRO for testing.",
        "success_code" to "VIP access loaded successfully. Thank you for your support!",
        
        "settings_title" to "Aesthetic Preferences",
        "settings_lang" to "Interface Translation Language",
        "settings_theme" to "Device Display Theme",
        "settings_light" to "Standard Balanced Light Mode",
        "settings_dark" to "Modern Midnight Dark Mode",
        "settings_overlay" to "System Overlay Window Service",
        "settings_overlay_desc" to "Runs an active foreground drawing context enabling you to copy URLs directly into your local database.",
        "settings_overlay_status" to "System Drawing Authorization Permission",
        "request_overlay" to "Authorize Permission",
        "start_service" to "Install Floating Icon",
        "stop_service" to "Remove Floating Icon",
        "about_title" to "About WebShield Pro",
        "about_desc" to "WebShield Pro is a full-featured client-side privacy framework dedicated to script block filters, ad shields, and localized multi-turn AI assistance.",
        "version" to "Product Build: Pro v4.2.1",
        "rights" to "All Rights Reserved © 2026"
    )
}

@Composable
fun MainScreen(viewModel: ExtensionViewModel) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val isActivated by viewModel.isActivated.collectAsState()

    val context = LocalContext.current
    val textMap = if (isArabic) Locales.AR else Locales.EN

    // Main bottom navigation tabs
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog state flags for Vault saving
    var showAddClipDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    // Desktop/Sandbox Overlay Simulator
    var isSandboxActive by remember { mutableStateOf(false) }
    var sandboxOffset by remember { mutableStateOf<Offset>(Offset(120f, 450f)) }
    var isSandboxExpanded by remember { mutableStateOf(false) }

    val direction = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text(textMap["tab_dashboard"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text(textMap["tab_quick_data"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Article, contentDescription = "Quick Data") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_quick_data")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text(textMap["tab_autofill"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Input, contentDescription = "Autofill") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_autofill")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        label = { Text(textMap["tab_ai"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_ai")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        label = { Text(textMap["tab_more"] ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_more")
                    )
                }
            },
            floatingActionButton = {
                // Keep the clipboard note saving buttons available on the Vault or more sections if appropriate
                if (selectedTab == 4) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { showAddClipDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            modifier = Modifier.testTag("floating_add_clip")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add URL")
                        }
                        FloatingActionButton(
                            onClick = { showAddNoteDialog = true },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White,
                            modifier = Modifier.testTag("floating_add_note")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Add Note")
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Title Header component
                    HeaderBar(isArabic = isArabic, title = textMap["app_title"] ?: "ويب شيلد برو", isVip = isActivated)

                    when (selectedTab) {
                        0 -> DashboardTab(
                            viewModel = viewModel,
                            textMap = textMap,
                            isSandboxActive = isSandboxActive,
                            onToggleSandbox = { isSandboxActive = it }
                        )
                        1 -> QuickDataTab(viewModel = viewModel, isArabic = isArabic)
                        2 -> AutofillTab(viewModel = viewModel, isArabic = isArabic)
                        3 -> AiChatTab(viewModel = viewModel, isArabic = isArabic)
                        4 -> {
                            var nestedSettingsTab by remember { mutableStateOf(0) }
                            
                            ScrollableTabRow(
                                selectedTabIndex = nestedSettingsTab,
                                containerColor = MaterialTheme.colorScheme.surface,
                                edgePadding = 12.dp
                            ) {
                                Tab(
                                    selected = nestedSettingsTab == 0,
                                    onClick = { nestedSettingsTab = 0 },
                                    text = { Text(textMap["tab_stats"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = nestedSettingsTab == 1,
                                    onClick = { nestedSettingsTab = 1 },
                                    text = { Text(textMap["tab_logs"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = nestedSettingsTab == 2,
                                    onClick = { nestedSettingsTab = 2 },
                                    text = { Text(textMap["tab_libs"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = nestedSettingsTab == 3,
                                    onClick = { nestedSettingsTab = 3 },
                                    text = { Text(textMap["tab_vault"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = nestedSettingsTab == 4,
                                    onClick = { nestedSettingsTab = 4 },
                                    text = { Text(textMap["tab_settings"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = nestedSettingsTab == 5,
                                    onClick = { nestedSettingsTab = 5 },
                                    text = { Text(textMap["tab_about"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = nestedSettingsTab == 6,
                                    onClick = { nestedSettingsTab = 6 },
                                    text = { Text(textMap["tab_activation"] ?: "", fontWeight = FontWeight.Bold) }
                                )
                            }

                            when (nestedSettingsTab) {
                                0 -> StatsTab(viewModel = viewModel, textMap = textMap)
                                1 -> AppLogsTab(viewModel = viewModel, textMap = textMap)
                                2 -> LibrariesTab(viewModel = viewModel, textMap = textMap)
                                3 -> VaultTabCombined(viewModel = viewModel, textMap = textMap)
                                4 -> SettingsTab(viewModel = viewModel, textMap = textMap)
                                5 -> AboutSection(isArabic = isArabic)
                                6 -> ActivationTab(viewModel = viewModel, textMap = textMap)
                            }
                        }
                    }
                }

                // --- Sandbox Simulated Floating Bubble ---
                if (isSandboxActive) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(sandboxOffset.x.roundToInt(), sandboxOffset.y.roundToInt()) }
                            .shadow(12.dp, RoundedCornerShape(24.dp))
                    ) {
                        if (!isSandboxExpanded) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    )
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDrag = { _, dragAmount ->
                                                sandboxOffset = Offset(
                                                    sandboxOffset.x + dragAmount.x,
                                                    sandboxOffset.y + dragAmount.y
                                                )
                                            }
                                        )
                                    }
                                    .clickable { isSandboxExpanded = true }
                                    .testTag("sandbox_bubble")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Sandbox Shield",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .width(320.dp)
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDrag = { _, dragAmount ->
                                                sandboxOffset = Offset(
                                                    sandboxOffset.x + dragAmount.x,
                                                    sandboxOffset.y + dragAmount.y
                                                )
                                            }
                                        )
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isArabic) "جدار الحماية المصغر" else "Shield Sandbox",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Row {
                                            IconButton(
                                                onClick = { isSandboxExpanded = false },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Minimize,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { isSandboxActive = false },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isArabic) "هذه أداة محاكاة تفاعلية لفحص وحقن السكريبتات فوق نظام التشغيل دون مغادرة التطبيق الحالي." 
                                               else "This is an overlay simulation to practice script injections and web clippings on-the-fly.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, if (isArabic) "تم محاكاة تصفية الرابط!" else "Simulated Link Check!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (isArabic) "تصفية رابط نشط" else "Filter Current Link")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Dialog layouts ---
        if (showAddClipDialog) {
            var clipUrl by remember { mutableStateOf("") }
            var clipTitle by remember { mutableStateOf("") }
            var clipComment by remember { mutableStateOf("") }
            var clipCat by remember { mutableStateOf("General") }

            AlertDialog(
                onDismissRequest = { showAddClipDialog = false },
                title = { Text(textMap["add_clip"] ?: "", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = clipUrl,
                            onValueChange = { clipUrl = it },
                            label = { Text("URL Link") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = clipTitle,
                            onValueChange = { clipTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = clipComment,
                            onValueChange = { clipComment = it },
                            label = { Text("Note / Comment") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (clipUrl.isNotEmpty()) {
                            viewModel.insertClip(clipUrl, clipTitle, clipComment, clipCat)
                            showAddClipDialog = false
                        }
                    }) {
                        Text("حفظ / Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddClipDialog = false }) {
                        Text("إلغاء / Cancel")
                    }
                }
            )
        }

        if (showAddNoteDialog) {
            var noteTitle by remember { mutableStateOf("") }
            var noteBody by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddNoteDialog = false },
                title = { Text(textMap["add_note"] ?: "", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = noteBody,
                            onValueChange = { noteBody = it },
                            label = { Text("Notepad Content") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (noteBody.isNotEmpty()) {
                            viewModel.insertNote(noteTitle, noteBody)
                            showAddNoteDialog = false
                        }
                    }) {
                        Text("حفظ / Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddNoteDialog = false }) {
                        Text("إلغاء / Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun HeaderBar(isArabic: Boolean, title: String, isVip: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isArabic) "جدار حماية ذكي وأداة ملء تلقائي" else "Secure Firewall & Automatic Filler",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        if (isVip) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "PRO VIP",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ==========================================
// TABS 0: DASHBOARD (لوحة التحكم الرئيسية)
// ==========================================
@Composable
fun DashboardTab(
    viewModel: ExtensionViewModel,
    textMap: Map<String, String>,
    isSandboxActive: Boolean,
    onToggleSandbox: (Boolean) -> Unit
) {
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val adBlockStrict by viewModel.adBlockStrict.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Active firewall health status card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = textMap["service_status"] ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isServiceActive) (textMap["active"] ?: "") else (textMap["inactive"] ?: ""),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Switch(
                            checked = isServiceActive,
                            onCheckedChange = { viewModel.setServiceActive(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .offset(x = (-8).dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary)
                            )
                        }
                        Text(
                            text = textMap["threats_filtered"] ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Toggles Grid Layout
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setAdBlockStrict(!adBlockStrict) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Block, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isArabic) "حظر النوافذ المنبثقة" else "Popups Filter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (adBlockStrict) "وضع الفحص الصارم" else "تحذير فقط / Warn Only",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleSandbox(!isSandboxActive) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Extension, null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isArabic) "الأيقونة المصغرة" else "Shield Overlay Sandbox",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isSandboxActive) "نشطة الآن على الشاشة" else "اضغط لتجربة المحاكاة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // General System information card
        item {
            Card(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "حماية متكاملة بخصوصية مطلقة" else "Total Threat Protection Security",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isArabic) 
                            "يقوم التطبيق بفحص تصفية الإعلانات وحظر نصوص الخرائط وتتبع الإعجاب والملفات الخبيثة محلياً على جهازك دون إرسال أي بيانات للخوادم الخارجية."
                            else "All blocking rules, scripts tracking modules and accessibility auto filler engines run fully locally on your dynamic device with 0 external tracking.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// TABS 1: QUICK DATA (البيانات السريعة)
// ==========================================
@Composable
fun QuickDataTab(viewModel: ExtensionViewModel, isArabic: Boolean) {
    val quickFields by viewModel.quickFields.collectAsState()
    var rawInputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Compute lines & parsing count
    val lineCount = if (rawInputText.isEmpty()) 0 else rawInputText.split("\n").size
    val detectedCount = rawInputText.split("\n").filter { it.contains("=") && it.split("=").size >= 2 }.size

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Quick Data Header
        item {
            Column {
                Text(
                    text = if (isArabic) "تحليل وحفظ البيانات السريعة" else "Rapid Raw Text Field Parser",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) 
                        "الصق البيانات النصية المتعددة الأسطر وسيقوم جدار شيلد بتحليلها لمدخلات تعبئة تلقائية مصنفة وحفظها محلياً."
                        else "Paste massive text strings to parse structured fields for auto filling services locally.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Multi-line Text Area Inputs container
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isArabic) "مربع الإدخال السريع (يدعم آلاف الأسطر)" else "Raw String Input Area (Multi-line Support)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rawInputText,
                        onValueChange = { rawInputText = it },
                        placeholder = {
                            Text(
                                text = if (isArabic) "مثال:\nالاسم=محمد أحمد\nالبريد=test@example.com\nالهاتف=053323049" 
                                       else "key=value list here to extract..."
                            )
                        },
                        minLines = 6,
                        maxLines = 15,
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row counts labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "عدد الأسطر: $lineCount" else "Lines: $lineCount",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isArabic) "الحقول المكتشفة: $detectedCount" else "Detected Fields: $detectedCount",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        // Action controls
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (rawInputText.isNotBlank()) {
                            viewModel.parseAndSaveQuickData(rawInputText)
                            Toast.makeText(context, if (isArabic) "تم حفظ وتحليل البيانات الكبيرة!" else "Successfully parsed!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isArabic) "حفظ وتحليل" else "Parse & Save")
                }

                OutlinedButton(
                    onClick = {
                        // Import sample dummy templates
                        rawInputText = if (isArabic) {
                            "الاسم=مروان أحمد\nالبريد=marwan@example.com\nالهاتف=777888999\nالعنوان=شارع الستين\nالمدينة=صنعاء\nالدولة=اليمن\nالعمر=30"
                        } else {
                            "name=Marwan Ahmed\nemail=marwan@example.com\nphone=777888999\naddress=Sixty Street\ncity=Sanaa\ncountry=Yemen\nage=30"
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isArabic) "استيراد قالب" else "Import Template")
                }
            }
        }

        // Fields Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "البيانات النشطة المجرّدة" else "Active Extracted Fields",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                if (quickFields.isNotEmpty()) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = { viewModel.clearAllQuickFields() }
                    ) {
                        Icon(Icons.Default.DeleteSweep, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "مسح البيانات" else "Clear Data")
                    }
                }
            }
        }

        // Display parsed items in card list
        if (quickFields.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isArabic) "قاعدة البيانات فارغة حالياً. أدخل مفتاح وقيمة أعلاه." 
                                   else "Local Quick Database is empty. Paste template and save.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(quickFields) { field ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (field.category) {
                                            "name" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            "email" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                            "phone" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                            "address" -> MaterialTheme.colorScheme.primaryContainer
                                            else -> MaterialTheme.colorScheme.inverseOnSurface
                                        }
                                    )
                            ) {
                                val icon = when (field.category) {
                                    "name" -> Icons.Default.Person
                                    "email" -> Icons.Default.Email
                                    "phone" -> Icons.Default.Phone
                                    "address" -> Icons.Default.Place
                                    else -> Icons.Default.Label
                                }
                                Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = field.key,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = field.value,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteQuickField(field.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Clear, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 2: AUTOFILL (التعبئة التلقائية)
// ==========================================
@Composable
fun AutofillTab(viewModel: ExtensionViewModel, isArabic: Boolean) {
    val autofillActions by viewModel.autofillActions.collectAsState()
    val context = LocalContext.current

    // Dynamically check activation status of accessibility service
    var isEnabledInSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val expectedComponentName = ComponentName(context, WebShieldAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
        isEnabledInSettings = enabledServices.contains(expectedComponentName.flattenToString())
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Accessibility Header
        item {
            Column {
                Text(
                    text = if (isArabic) "محرّك التعبئة التلقائية الذكية" else "Automated Autofill Shield",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) 
                        "تتيح خدمة الـ Accessibility اكتشاف الحقول النصية والرقمية والبريد والهاتف وتعبئتها مع إرسال النوذج بضغطة واحدة."
                        else "Uses Android Accessibility APIs to detect input forms and fill them completely locally.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // State & Permission card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEnabledInSettings) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "حالة خدمة المساعدة" else "Accessibility Utility Status",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEnabledInSettings) (if (isArabic) "نشطة وتصاحب الحقول" else "Service Active") else (if (isArabic) "غير مفعلة بعد" else "Service Inactive"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Icon(
                            imageVector = if (isEnabledInSettings) Icons.Default.TaskAlt else Icons.Default.Warning,
                            null,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isArabic) 
                            "تتطلب الخدمة منح الإذن للظهور والتحلي بالفحص السطحي للحقول. اضغط على الزر أدناه ثم فعّل WebShield Pro."
                            else "Click the button below to navigate to settings and activate 'WebShield Pro - Automatic Autofill Service'.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = LocalContentColor.current.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "الرجاء الذهاب يدوياً للإعدادات", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isArabic) "منح إذن خدمة التعبئة" else "Configure System Accessibility")
                    }
                }
            }
        }

        // Live stats counters
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val totalDetected = autofillActions.sumOf { it.fieldsDetected }
                val totalFilled = autofillActions.sumOf { it.fieldsFilled }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isArabic) "الحقول المكتشفة" else "Detected Fields",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalDetected",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isArabic) "حقول تم ملؤها" else "Filled Fields",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalFilled",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        // Action History Logs Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "أرشيف عمليات التعبئة" else "Form Filling Audit Trail",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                if (autofillActions.isNotEmpty()) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = { viewModel.clearAutofillHistory() }
                    ) {
                        Text(if (isArabic) "مسح السجل" else "Clear History")
                    }
                }
            }
        }

        // Fill list logs
        if (autofillActions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isArabic) "لا توجد حركات تعبئة مسجلة حالياً." else "No autofill history verified yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            items(autofillActions) { action ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = action.appPackage,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) "تمت تعبئة ${action.fieldsFilled} حقل من ${action.fieldsDetected}" 
                                       else "Filled ${action.fieldsFilled} fields of ${action.fieldsDetected}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(action.timestamp))
                        Text(
                            text = timeStr,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 3: ARTIFICIAL INTELLIGENCE (المساعد الذكي)
// ==========================================
@Composable
fun AiChatTab(viewModel: ExtensionViewModel, isArabic: Boolean) {
    val conversations by viewModel.chatConversations.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    val context = LocalContext.current

    // Local Chat Settings expand controller
    var showEngineConfig by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // AI Title section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) "مساعد الأمان الذكي" else "AI Security Copilot",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "ساعد بمكافحة التهديدات، وصياغة، وتلخيص المقالات العائمة." 
                               else "Ask Gemini or secure REST APIs to process Web Clips.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showEngineConfig = !showEngineConfig },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (showEngineConfig) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Optional Engine Setting Drawer config panel
        if (showEngineConfig) {
            item {
                EngineConfigCard(viewModel = viewModel, isArabic = isArabic)
            }
        }

        // Conversations tracks horizontal listing
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "مسارات الأحاديث النشطة" else "Conversational Threads",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                TextButton(
                    onClick = { viewModel.createNewChat("") }
                ) {
                    Icon(Icons.Default.AddComment, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "محادثة جديدة" else "New Chat")
                }
            }
        }

        // Horizontal chat capsules
        if (conversations.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    conversations.forEach { conv ->
                        val selected = activeConversationId == conv.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.selectConversation(conv.id) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conv.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (selected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable { viewModel.deleteChat(conv.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // If no active conversations
        if (activeConversationId == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.QuestionAnswer, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isArabic) "ابدأ مساراً جديداً للمناقشة" else "Initialize a security dialog to consult",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            shape = RoundedCornerShape(10.dp),
                            onClick = { viewModel.createNewChat("") }
                        ) {
                            Text(if (isArabic) "إنشاء مسار مناقشة" else "Consult Engine Now")
                        }
                    }
                }
            }
        } else {
            // Active message cards list
            if (activeMessages.isEmpty()) {
                item {
                    Text(
                        text = if (isArabic) "المحادثة فارغة. ابدأ في كتابة أسئلتك الأمنية أدناه." 
                               else "Thread is draft. Type any text or use rapid prompt shortcuts below.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()
                    )
                }
            } else {
                items(activeMessages) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 0.dp,
                                        bottomEnd = if (isUser) 0.dp else 16.dp
                                    )
                                )
                                .background(
                                    if (isUser) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(12.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Column {
                                SelectionContainer {
                                    Text(
                                        text = msg.content,
                                        fontSize = 13.sp,
                                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = (if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable {
                                                val clip = ClipData.newPlainText("AI message", msg.content)
                                                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                                                Toast.makeText(context, if (isArabic) "تم نسخ الرد!" else "Copied!", Toast.LENGTH_SHORT).show()
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Spinner loader
            if (aiLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text(
                            text = if (isArabic) "جاري استشارة الذكاء الاصطناعي..." else "Copilot is analyzing text...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Prompt Helpers (تلخيص، ترجمة، إعادة صياغة)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val promptCategories = listOf(
                        "تلخيص النص" to if (isArabic) "لخص لي هذا النص أمنياً بالتفصيل:" else "Summarize this secure article:",
                        "إعادة صياغة" to if (isArabic) "أعد كتابة هذا النص بأسلوب احترافي:" else "Rephrase this content professionally:",
                        "ترجمة العربية" to if (isArabic) "ترجم هذا النص للغة العربية بدقة:" else "Translate this text carefully to Arabic:",
                        "تحليل البيانات" to if (isArabic) "حلل هذه البيانات السريعة واكتشف الأنماط أمنياً:" else "Analyze this raw text list:"
                    )

                    promptCategories.forEach { (title, prompt) ->
                        OutlinedButton(
                            onClick = {
                                viewModel.sendChatMessage(prompt)
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Chat input field triggers
            item {
                var messageInput by remember { mutableStateOf("") }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text(if (isArabic) "اكتب سؤالك الأمني..." else "Ask any task to AI...") },
                            maxLines = 3,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        IconButton(
                            enabled = messageInput.isNotBlank() && !aiLoading,
                            onClick = {
                                viewModel.sendChatMessage(messageInput)
                                messageInput = ""
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EngineConfigCard(viewModel: ExtensionViewModel, isArabic: Boolean) {
    val aiUrl by viewModel.aiApiUrl.collectAsState()
    val aiKey by viewModel.aiApiKey.collectAsState()
    val aiModel by viewModel.aiModelName.collectAsState()
    val aiTemp by viewModel.aiTemperature.collectAsState()
    val aiMaxTokens by viewModel.aiMaxTokens.collectAsState()
    val aiTimeout by viewModel.aiTimeout.collectAsState()
    val connectionStatus by viewModel.aiConnectionStatus.collectAsState()

    var editingUrl by remember { mutableStateOf(aiUrl) }
    var editingKey by remember { mutableStateOf(aiKey) }
    var editingModel by remember { mutableStateOf(aiModel) }
    var editingTemp by remember { mutableStateOf(aiTemp) }
    var editingMaxTokens by remember { mutableStateOf(aiMaxTokens) }
    var editingTimeout by remember { mutableStateOf(aiTimeout) }

    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (isArabic) "إعدادات محرك الذكاء الاصطناعي الذاتية" else "API Engine Customizations",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = editingUrl,
                onValueChange = { editingUrl = it },
                label = { Text("API Base URL") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = editingKey,
                onValueChange = { editingKey = it },
                label = { Text("API Key (Decompiled Secure)") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editingModel,
                    onValueChange = { editingModel = it },
                    label = { Text("Model Name") },
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = "${editingTimeout}s",
                    onValueChange = { 
                        editingTimeout = it.replace("s", "").toIntOrNull() ?: 15 
                    },
                    label = { Text("Timeout") },
                    maxLines = 1,
                    modifier = Modifier.width(80.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "حرارة المدخلات / Temp: ${String.format(Locale.ENGLISH, "%.1f", editingTemp)}" 
                           else "Temperature: ${String.format(Locale.ENGLISH, "%.1f", editingTemp)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = editingTemp,
                    onValueChange = { editingTemp = it },
                    valueRange = 0f..1.5f,
                    modifier = Modifier.width(180.dp)
                )
            }

            // Connection Status and Actions
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الحالة / Stat: $connectionStatus",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        onClick = {
                            viewModel.setAiSettings(
                                editingUrl, editingKey, editingModel, editingTemp, editingMaxTokens, editingTimeout
                            )
                            viewModel.testAIConnection()
                        }
                    ) {
                        Text(if (isArabic) "حفظ واختبار" else "Save & Test", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 4: EXTRA TAB VIEW -> ABOUT DEVELOPMENT (حول التطبيق)
// ==========================================
@Composable
fun AboutSection(isArabic: Boolean) {
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Shield, null, modifier = Modifier.size(40.dp), tint = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "WebShield Pro",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "الإصدار الذهبي المتكامل v4.2.1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isArabic) 
                            "نظام الحماية المتكامل الأوفر سعة لحظر الإعلانات، وعزل النوافذ المنبثقة، وتعبئة الحقول أوتوماتيكياً محلياً مع المساعد الذكي."
                            else "A high-performance offline privacy guardian built for rapid web clipping, automated form fillings, and secure sandbox script operations.",
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Developer info
        item {
            Card(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "معلومات المطوّر / Creator Profiles" else "Developer Specifications",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isArabic) "برمجة وبناء: مروان أحمد" else "Architected by: Marwan Ahmed",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Communication buttons
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/967733234211"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "تعذر فتح تطبيق واتساب ريما غير مثبت", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تواصل عبر واتساب (+967733234211)")
                        }

                        Button(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/CYBBEEAGLE"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "تعذر فتح تليجرام", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("قناتنا على تليجرام (CYBBEEAGLE)")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 1: STATS (الإحصائيات)
// ==========================================
@Composable
fun StatsTab(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val isArabic by viewModel.isArabic.collectAsState()
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = textMap["stats_title"] ?: "تصفية التهديدات",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isArabic) "معدل الحماية الإجمالي اليوم" else "Daily Shield Ratio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                        CircularProgressIndicator(
                            progress = { 0.98f },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 10.dp,
                            color = MaterialTheme.colorScheme.primary,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("98%", fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text(if (isArabic) "آمن جداً" else "Secured", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(textMap["stats_adblock"] ?: "Ads", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("4,102", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(textMap["stats_popups"] ?: "Popups", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("312", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 2: APP LOGS (منبه الأمان الذكي والـ Watchdog)
// ==========================================
@Composable
fun AppLogsTab(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val logs by viewModel.appLogs.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = textMap["logs_title"] ?: "سجلات الحماية",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { viewModel.clearLogs() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(textMap["logs_clear"] ?: "مسح")
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(textMap["logs_empty"] ?: "", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            items(logs) { log ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = when (log.level) {
                                        "BLOCK" -> MaterialTheme.colorScheme.errorContainer
                                        "WARN" -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.primaryContainer
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = log.level,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = log.tag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = log.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        val timeString = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(log.timestamp))
                        Text(text = timeString, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 3: VAULT (الروابط والملاحظات المخزنة)
// ==========================================
@Composable
fun VaultTabCombined(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val clips by viewModel.webClips.collectAsState()
    val notes by viewModel.quickNotes.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activeSubTab) {
            Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }) {
                Text(if (isArabic) "الروابط الملقطة" else "Clipped URLs", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }) {
                Text(if (isArabic) "المفكّرة السريعة" else "Secure Notes", modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeSubTab == 0) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (clips.isEmpty()) {
                    item {
                        Text(
                            textMap["logs_empty"] ?: "فارغ",
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(clips) { clip ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = clip.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = clip.url, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (clip.note.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = clip.note, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    onClick = { viewModel.deleteClip(clip) }
                                ) {
                                    Text(if (isArabic) "حذف الحزمة" else "Delete")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (notes.isEmpty()) {
                    item {
                        Text(
                            textMap["logs_empty"] ?: "فارغ",
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(notes) { note ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = note.body, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    onClick = { viewModel.deleteNote(note) }
                                ) {
                                    Text(if (isArabic) "ازالة" else "Remove")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 4: SETTINGS (إعدادات النظام العامة)
// ==========================================
@Composable
fun SettingsTab(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val overlayOpacity by viewModel.overlayOpacity.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = textMap["settings_title"] ?: "Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Language Select
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = textMap["settings_lang"] ?: "Language", fontWeight = FontWeight.Bold)
                    Row {
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isArabic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                            onClick = { viewModel.setArabic(true) }
                        ) {
                            Text("العربية", color = if (isArabic) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (!isArabic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                            onClick = { viewModel.setArabic(false) }
                        ) {
                            Text("English", color = if (!isArabic) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        // Dark Theme Select
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = textMap["settings_theme"] ?: "Theme", fontWeight = FontWeight.Bold)
                    Switch(checked = isDarkTheme, onCheckedChange = { viewModel.setDarkTheme(it) })
                }
            }
        }

        // Active Foreground Float Trigger configuration
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = textMap["settings_overlay"] ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = textMap["settings_overlay_desc"] ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Trigger overlay drawing authorize
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = textMap["settings_overlay_status"] ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                            }
                        ) {
                            Text(textMap["request_overlay"] ?: "")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                context.startService(Intent(context, FloatingOverlayService::class.java))
                                Toast.makeText(context, "تم تشغيل الأيقونة العائمة!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(textMap["start_service"] ?: "Start")
                        }

                        Button(
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            onClick = {
                                context.stopService(Intent(context, FloatingOverlayService::class.java))
                                Toast.makeText(context, "تم إيقاف الخدمة!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(textMap["stop_service"] ?: "Stop")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 5: LIBRARIES MANAGER (إدارة مكتبات التصفية)
// ==========================================
@Composable
fun LibrariesTab(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val libs by viewModel.librariesList.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    var newLibName by remember { mutableStateOf("") }
    var newLibVer by remember { mutableStateOf("v1.0") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = textMap["libs_title"] ?: "Libraries", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = textMap["libs_desc"] ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Add Rule
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = textMap["libs_add"] ?: "Add", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLibName,
                        onValueChange = { newLibName = it },
                        label = { Text(textMap["lib_name"] ?: "") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newLibVer,
                        onValueChange = { newLibVer = it },
                        label = { Text(textMap["lib_version"] ?: "") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            if (newLibName.isNotBlank()) {
                                viewModel.injectLibraryRule(newLibName, newLibVer)
                                newLibName = ""
                            }
                        }
                    ) {
                        Text(textMap["libs_add"] ?: "")
                    }
                }
            }
        }

        items(libs) { lib ->
            Card {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = lib.first, fontWeight = FontWeight.Bold)
                        Text(text = lib.second, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = { viewModel.removeLibraryRule(lib.first) }
                    ) {
                        Text(if (isArabic) "إزالة" else "Uninstall")
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 6: FILE MANAGER (أرشيف الملفات المحلية)
// ==========================================
@Composable
fun FileManagerTab(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val isArabic by viewModel.isArabic.collectAsState()
    val context = LocalContext.current

    val localFiles = remember {
        listOf(
            "rules_adblock_fallback.json" to "42 KB",
            "malicious_scripts_manifest.db" to "128 KB",
            "cookie_filters_whitelist.txt" to "14 KB"
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = textMap["files_title"] ?: "Files", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = textMap["files_desc"] ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(localFiles) { file ->
            Card {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FilePresent, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = file.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = file.second, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            Toast.makeText(context, if (isArabic) "تم تحديث الملف وتنزيل الروافد!" else "Synchronized successfully!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(if (isArabic) "تحديث" else "Sync")
                    }
                }
            }
        }
    }
}

// ==========================================
// TABS 7: ACTIVATION LICENSE MANAGER (تفعيل الترخيص)
// ==========================================
@Composable
fun ActivationTab(viewModel: ExtensionViewModel, textMap: Map<String, String>) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isVip by viewModel.isActivated.collectAsState()
    val context = LocalContext.current

    var codeInput by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(text = textMap["activation_title"] ?: "Activation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = textMap["activation_desc"] ?: "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (isVip) {
                        Text(text = textMap["active_vip"] ?: "Active VIP Lifetime", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = if (isArabic) "شكراً لاستخدامك حزمة شيلد برو، العضوية نشطة وصالحة لجميع التحديثات." else "Premium features lifetime unlocked.", fontSize = 12.sp)
                    } else {
                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it },
                            label = { Text(textMap["activation_code"] ?: "License Key") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                if (codeInput.trim().uppercase() == "SHIELD-2026-PRO") {
                                    viewModel.setActivated(true)
                                    actionMessage = textMap["success_code"] ?: ""
                                } else {
                                    actionMessage = textMap["invalid_code"] ?: ""
                                }
                            }
                        ) {
                            Text(textMap["activate"] ?: "")
                        }
                    }

                    if (actionMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = actionMessage, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isVip) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
