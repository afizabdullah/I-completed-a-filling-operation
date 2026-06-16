package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExtensionViewModel(
    application: Application,
    private val repository: ExtensionRepository
) : AndroidViewModel(application) {

    // Preferences States (Using SharedPreferences)
    private val prefs = application.getSharedPreferences("web_companion_prefs", Context.MODE_PRIVATE)

    private val _isArabic = MutableStateFlow(prefs.getBoolean("is_arabic", true)) // default to Arabic (RTL)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark", true)) // default to Elegant Dark
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isServiceActive = MutableStateFlow(prefs.getBoolean("is_active", true)) // default to Active
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    private val _isActivated = MutableStateFlow(prefs.getBoolean("is_activated", false))
    val isActivated: StateFlow<Boolean> = _isActivated.asStateFlow()

    private val _adBlockStrict = MutableStateFlow(prefs.getBoolean("ad_block_strict", true))
    val adBlockStrict: StateFlow<Boolean> = _adBlockStrict.asStateFlow()

    private val _overlayOpacity = MutableStateFlow(prefs.getFloat("overlay_opacity", 0.85f))
    val overlayOpacity: StateFlow<Float> = _overlayOpacity.asStateFlow()

    // --- AI Configuration Preferences ---
    private val _aiApiUrl = MutableStateFlow(prefs.getString("ai_api_url", "https://generativelanguage.googleapis.com") ?: "https://generativelanguage.googleapis.com")
    val aiApiUrl: StateFlow<String> = _aiApiUrl.asStateFlow()

    private val _aiApiKey = MutableStateFlow(prefs.getString("ai_api_key", "") ?: "")
    val aiApiKey: StateFlow<String> = _aiApiKey.asStateFlow()

    private val _aiModelName = MutableStateFlow(prefs.getString("ai_model_name", "gemini-3.5-flash") ?: "gemini-3.5-flash")
    val aiModelName: StateFlow<String> = _aiModelName.asStateFlow()

    private val _aiTemperature = MutableStateFlow(prefs.getFloat("ai_temperature", 0.7f))
    val aiTemperature: StateFlow<Float> = _aiTemperature.asStateFlow()

    private val _aiMaxTokens = MutableStateFlow(prefs.getInt("ai_max_tokens", 1024))
    val aiMaxTokens: StateFlow<Int> = _aiMaxTokens.asStateFlow()

    private val _aiTimeout = MutableStateFlow(prefs.getInt("ai_timeout", 15))
    val aiTimeout: StateFlow<Int> = _aiTimeout.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiConnectionStatus = MutableStateFlow("لم يتم الاختبار بعد / Not Tested")
    val aiConnectionStatus: StateFlow<String> = _aiConnectionStatus.asStateFlow()

    // Library Scripts List (Managed in Memory with Persistence soon if needed)
    private val _librariesList = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "AdBlock Basic Filters" to "v2.1",
            "Malicious Redirect Blocker" to "v4.0",
            "Cookie Privacy Shield" to "v1.5",
            "Advanced Script Sandbox" to "v3.2"
        )
    )
    val librariesList: StateFlow<List<Pair<String, String>>> = _librariesList.asStateFlow()

    // Interactive Fields Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Observe DB Datastores
    val webClips: StateFlow<List<WebClip>> = combine(
        repository.allClips,
        _searchQuery,
        _selectedCategory
    ) { clips, query, category ->
        var filtered = clips
        if (category != "All") {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.url.contains(query, ignoreCase = true) ||
                it.note.contains(query, ignoreCase = true)
            }
        }
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quickNotes: StateFlow<List<QuickNote>> = combine(
        repository.allNotes,
        _searchQuery
    ) { notes, query ->
        if (query.isEmpty()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.body.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val appLogs: StateFlow<List<AppLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quickFields: StateFlow<List<QuickDataField>> = repository.allQuickFields
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val chatConversations: StateFlow<List<ChatConversation>> = repository.allConversations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val autofillActions: StateFlow<List<AutofillAction>> = repository.allAutofillActions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Selected Conversation & Reactive message flow
    private val _activeConversationId = MutableStateFlow<Int?>(null)
    val activeConversationId: StateFlow<Int?> = _activeConversationId.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeMessages: StateFlow<List<ChatMessage>> = _activeMessages.asStateFlow()

    private var messagesJob: Job? = null

    init {
        viewModelScope.launch {
            repository.allClips.collect { clips ->
                if (clips.isEmpty()) {
                    seedInitialData()
                }
            }
        }

        // Listen for active conversation switches
        viewModelScope.launch {
            _activeConversationId.collect { id ->
                messagesJob?.cancel()
                if (id != null) {
                    messagesJob = viewModelScope.launch {
                        repository.getMessagesForConversation(id).collect { msgs ->
                            _activeMessages.value = msgs
                        }
                    }
                } else {
                    _activeMessages.value = emptyList()
                }
            }
        }
    }

    private suspend fun seedInitialData() {
        val defaultClips = listOf(
            WebClip(
                title = "Android Jetpack Compose Guide",
                url = "https://developer.android.com/compose",
                note = "Essential reference for building Material 3 layouts and state management safely in modern Android applications.",
                category = "General"
            ),
            WebClip(
                title = "Material Design 3 Components",
                url = "https://m3.material.io",
                note = "Visual style guide, color palettes, and component behavior standards for pristine, user-friendly layouts.",
                category = "Work"
            ),
            WebClip(
                title = "Kotlin Coroutines & Flow",
                url = "https://kotlinlang.org/docs/coroutines-overview.html",
                note = "Reactive asynchronous data flow documentation. Covers stateIn, combine, and collectAsStateWithLifecycle.",
                category = "Read Later"
            )
        )
        for (clip in defaultClips) {
            repository.insertClip(clip)
        }

        val defaultNotes = listOf(
            QuickNote(
                title = "Ideas: Extension Companion Features",
                body = "1. Floating Overlay Bubble for quick clipping during browsing.\n2. Clipboard auto-detection to easily extract URLs.\n3. Custom tags with nice color presets.\n4. Light and Dark Theme transitions."
            ),
            QuickNote(
                title = "Useful Code Snippet",
                body = "Modifier.windowInsetsPadding(WindowInsets.safeDrawing)\n// Ensures layout is kept safely within notch and camera cutouts."
            )
        )
        for (note in defaultNotes) {
            repository.insertNote(note)
        }

        val defaultLogs = listOf(
            AppLog(level = "INFO", tag = "System", message = "بدء تشغيل جدار حماية WebShield Pro بنجاح."),
            AppLog(level = "BLOCK", tag = "AdBlock", message = "تم تفعيل وضع تصفية الإعلانات الصارم."),
            AppLog(level = "INFO", tag = "Overlay", message = "خدمة الأيقونة العائمة للأمان جاهزة للاستخدام."),
            AppLog(level = "WARN", tag = "Security", message = "جدار الحماية نشط ومستعد لفحص الثغرات.")
        )
        for (log in defaultLogs) {
            repository.insertLog(log)
        }
    }

    // --- Preferences Actions ---
    fun setArabic(value: Boolean) {
        _isArabic.value = value
        prefs.edit().putBoolean("is_arabic", value).apply()
        insertLog("INFO", "System", if (value) "تم تغيير لغة النظام إلى العربية" else "System language changed to English")
    }

    fun setDarkTheme(value: Boolean) {
        _isDarkTheme.value = value
        prefs.edit().putBoolean("is_dark", value).apply()
        insertLog("INFO", "System", if (value) "تم اختيار المظهر الداكن الأنيق" else "Light theme selected")
    }

    fun setServiceActive(value: Boolean) {
        _isServiceActive.value = value
        prefs.edit().putBoolean("is_active", value).apply()
        insertLog("INFO", "Service", if (value) "تم تفعيل حماية جدار الحماية بنجاح" else "Firewall protection suspended")
    }

    fun setActivated(value: Boolean) {
        _isActivated.value = value
        prefs.edit().putBoolean("is_activated", value).apply()
    }

    fun setAdBlockStrict(value: Boolean) {
        _adBlockStrict.value = value
        prefs.edit().putBoolean("ad_block_strict", value).apply()
        insertLog("INFO", "AdBlock", if (value) "تم تفعيل وضع الفحص والمنع الصارم" else "Lenient block rules activated")
    }

    fun setOverlayOpacity(value: Float) {
        _overlayOpacity.value = value
        prefs.edit().putFloat("overlay_opacity", value).apply()
    }

    fun setAiSettings(url: String, key: String, model: String, temperature: Float, maxTokens: Int, timeout: Int) {
        _aiApiUrl.value = url
        _aiApiKey.value = key
        _aiModelName.value = model
        _aiTemperature.value = temperature
        _aiMaxTokens.value = maxTokens
        _aiTimeout.value = timeout

        prefs.edit()
            .putString("ai_api_url", url)
            .putString("ai_api_key", key)
            .putString("ai_model_name", model)
            .putFloat("ai_temperature", temperature)
            .putInt("ai_max_tokens", maxTokens)
            .putInt("ai_timeout", timeout)
            .apply()

        insertLog("INFO", "AI", "تم حفظ إعدادات المحرك الذكي")
    }

    // --- Search query control ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- Logs Actions ---
    fun insertLog(level: String, tag: String, message: String) {
        viewModelScope.launch {
            repository.insertLog(AppLog(level = level, tag = tag, message = message))
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    // --- Web Clips Actions ---
    fun insertClip(url: String, title: String, note: String, category: String) {
        viewModelScope.launch {
            val finalTitle = title.ifEmpty { "موقع محفوظ" }
            repository.insertClip(
                WebClip(
                    url = url,
                    title = finalTitle,
                    note = note,
                    category = category.ifEmpty { "عام" }
                )
            )
            repository.insertLog(
                AppLog(level = "INFO", tag = "Vault", message = "تم حفظ عنصر جديد في الخزنة: $finalTitle")
            )
        }
    }

    fun updateClip(clip: WebClip) {
        viewModelScope.launch {
            repository.updateClip(clip)
            repository.insertLog(
                AppLog(level = "INFO", tag = "Vault", message = "تم تحديث العنصر: ${clip.title}")
            )
        }
    }

    fun deleteClip(clip: WebClip) {
        viewModelScope.launch {
            repository.deleteClip(clip)
            repository.insertLog(
                AppLog(level = "WARN", tag = "Vault", message = "تم حذف العنصر من الخزنة: ${clip.title}")
            )
        }
    }

    // --- Quick Notes Actions ---
    fun insertNote(title: String, body: String) {
        viewModelScope.launch {
            val finalTitle = title.ifEmpty { "ملاحظة سريعة" }
            repository.insertNote(
                QuickNote(
                    title = finalTitle,
                    body = body
                )
            )
            repository.insertLog(
                AppLog(level = "INFO", tag = "System", message = "تم إنشاء ملاحظة جديدة: $finalTitle")
            )
        }
    }

    fun updateNote(note: QuickNote) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: QuickNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
            repository.insertLog(
                AppLog(level = "WARN", tag = "System", message = "تم حذف ملاحظة: ${note.title}")
            )
        }
    }

    // --- Libraries Script Insertion ---
    fun injectLibraryRule(name: String, version: String) {
        val current = _librariesList.value.toMutableList()
        current.add(name to version)
        _librariesList.value = current
        insertLog("INFO", "AdBlock", "تم حقن حزمة برمجية جديدة: $name @ $version")
    }

    fun removeLibraryRule(name: String) {
        val current = _librariesList.value.toMutableList()
        current.removeAll { it.first == name }
        _librariesList.value = current
        insertLog("WARN", "AdBlock", "تم إزالة حزمة البرمجيات: $name")
    }

    // --- Quick Data (البيانات السريعة) Segment ---
    fun parseAndSaveQuickData(rawText: String) {
        viewModelScope.launch {
            if (rawText.isBlank()) return@launch

            val lines = rawText.split("\n")
            val fieldsToInsert = mutableListOf<QuickDataField>()
            var parsedCount = 0

            for (line in lines) {
                if (line.contains("=")) {
                    val parts = line.split("=", limit = 2)
                    val key = parts[0].trim()
                    val value = parts[1].trim()

                    if (key.isNotEmpty() && value.isNotEmpty()) {
                        // Intelligent categorizer for autofill accessibility
                        val category = when {
                            key.contains("الاسم", ignoreCase = true) || key.contains("name", ignoreCase = true) -> "name"
                            key.contains("البريد", ignoreCase = true) || key.contains("email", ignoreCase = true) || value.contains("@") -> "email"
                            key.contains("الهاتف", ignoreCase = true) || key.contains("جوال", ignoreCase = true) || key.contains("phone", ignoreCase = true) || key.contains("mobile", ignoreCase = true) -> "phone"
                            key.contains("العنوان", ignoreCase = true) || key.contains("شارع", ignoreCase = true) || key.contains("address", ignoreCase = true) || key.contains("دولة", ignoreCase = true) || key.contains("مدينة", ignoreCase = true) || key.contains("city", ignoreCase = true) || key.contains("country", ignoreCase = true) -> "address"
                            key.contains("العمر", ignoreCase = true) || key.contains("سن", ignoreCase = true) || key.contains("age", ignoreCase = true) -> "number"
                            else -> "generic"
                        }

                        fieldsToInsert.add(
                            QuickDataField(
                                key = key,
                                value = value,
                                category = category
                            )
                        )
                        parsedCount++
                    }
                }
            }

            if (fieldsToInsert.isNotEmpty()) {
                repository.insertQuickFields(fieldsToInsert)

                // Cache all key-values directly into SharedPreferences as simplified JSON so accessibility service can read in 1ms cleanly!
                val jsonCache = JSONObject()
                val refreshedFields = repository.allQuickFields.firstOrNull() ?: fieldsToInsert
                for (field in refreshedFields) {
                    jsonCache.put(field.key, field.value)
                }
                prefs.edit().putString("quick_autofill_data", jsonCache.toString()).apply()

                insertLog(
                    "INFO",
                    "Database",
                    "البيانات السريعة: تم حفظ وتحليل $parsedCount حقلاً بنجاح في قاعدة البيانات."
                )
            }
        }
    }

    fun clearAllQuickFields() {
        viewModelScope.launch {
            repository.clearQuickFields()
            prefs.edit().remove("quick_autofill_data").apply()
            insertLog("WARN", "Database", "تم مسح حقول البيانات السريعة بالكامل.")
        }
    }

    fun deleteQuickField(id: Int) {
        viewModelScope.launch {
            repository.deleteQuickFieldById(id)
            insertLog("INFO", "Database", "تم حذف حقل الفردي المميز")
        }
    }

    // --- Artificial Intelligence (المساعد الذكي) Module ---
    fun selectConversation(convId: Int?) {
        _activeConversationId.value = convId
    }

    fun createNewChat(title: String) {
        viewModelScope.launch {
            val actualTitle = title.ifEmpty { "محادثة جديدة " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }
            val newId = repository.insertConversation(ChatConversation(title = actualTitle))
            _activeConversationId.value = newId.toInt()
            insertLog("INFO", "AI", "تم إنشاء مسار محادثة ذكية جديدة: $actualTitle")
        }
    }

    fun deleteChat(convId: Int) {
        viewModelScope.launch {
            repository.deleteConversationById(convId)
            repository.deleteMessagesForConversation(convId)
            if (_activeConversationId.value == convId) {
                _activeConversationId.value = null
            }
            insertLog("WARN", "AI", "تم حذف سجل المحادثة بالكامل.")
        }
    }

    fun testAIConnection() {
        viewModelScope.launch {
            _aiConnectionStatus.value = "جاري الاتصال بالفحص... / Verifying..."
            val success = makeApiCallAndCheck("اختبار اتصال جدار الحماية / Verify Shield Status Connectivity")
            if (success) {
                _aiConnectionStatus.value = "متصل بنجاح! / Connection Successful ✨"
            } else {
                _aiConnectionStatus.value = "فشل الاتصال! تحقق من الرمز والعنوان / Check Configs ❌"
            }
        }
    }

    private suspend fun makeApiCallAndCheck(testPrompt: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val key = _aiApiKey.value
            val isGemini = _aiApiUrl.value.contains("googleapis.com")
            
            val urlString = if (isGemini) {
                "${_aiApiUrl.value}/v1beta/models/${_aiModelName.value}:generateContent?key=$key"
            } else {
                "${_aiApiUrl.value}/v1/chat/completions"
            }

            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = _aiTimeout.value * 1000
            conn.readTimeout = _aiTimeout.value * 1000
            conn.setRequestProperty("Content-Type", "application/json")
            
            if (!isGemini && key.isNotEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer $key")
            }

            conn.doOutput = true

            val requestBody = if (isGemini) {
                JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", testPrompt) })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", _aiTemperature.value)
                        put("maxOutputTokens", 15) // small for connection tests
                    })
                }
            } else {
                JSONObject().apply {
                    put("model", _aiModelName.value)
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", testPrompt)
                        })
                    })
                    put("max_tokens", 15)
                    put("temperature", _aiTemperature.value)
                }
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(requestBody.toString())
            writer.flush()
            writer.close()

            val responseCode = conn.responseCode
            conn.disconnect()
            responseCode in 200..299
        } catch (e: Exception) {
            false
        }
    }

    fun sendChatMessage(text: String) {
        val convId = _activeConversationId.value ?: return
        if (text.trim().isEmpty()) return

        viewModelScope.launch {
            // Save User message
            repository.insertChatMessage(
                ChatMessage(
                    conversationId = convId,
                    role = "user",
                    content = text
                )
            )
            repository.updateConversationTimestamp(convId, System.currentTimeMillis())

            _aiLoading.value = true

            // Gather complete localized history to enable perfect multi-turn intelligence
            val history = _activeMessages.value.toMutableList()
            // Add current message to history if not collected yet
            if (history.none { it.role == "user" && it.content == text }) {
                history.add(ChatMessage(conversationId = convId, role = "user", content = text))
            }

            val reply = withContext(Dispatchers.IO) {
                try {
                    val key = _aiApiKey.value
                    val isGemini = _aiApiUrl.value.contains("googleapis.com")
                    
                    val urlString = if (isGemini) {
                        "${_aiApiUrl.value}/v1beta/models/${_aiModelName.value}:generateContent?key=$key"
                    } else {
                        "${_aiApiUrl.value}/v1/chat/completions"
                    }

                    val url = URL(urlString)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.connectTimeout = _aiTimeout.value * 1000
                    conn.readTimeout = _aiTimeout.value * 1000
                    conn.setRequestProperty("Content-Type", "application/json")
                    
                    if (!isGemini && key.isNotEmpty()) {
                        conn.setRequestProperty("Authorization", "Bearer $key")
                    }

                    conn.doOutput = true

                    val requestBody = if (isGemini) {
                        JSONObject().apply {
                            put("contents", JSONArray().apply {
                                for (msg in history) {
                                    put(JSONObject().apply {
                                        put("role", if (msg.role == "user") "user" else "model")
                                        put("parts", JSONArray().apply {
                                            put(JSONObject().apply { put("text", msg.content) })
                                        })
                                    })
                                }
                            })
                            put("generationConfig", JSONObject().apply {
                                put("temperature", _aiTemperature.value)
                                put("maxOutputTokens", _aiMaxTokens.value)
                            })
                        }
                    } else {
                        JSONObject().apply {
                            put("model", _aiModelName.value)
                            put("messages", JSONArray().apply {
                                for (msg in history) {
                                    put(JSONObject().apply {
                                        put("role", msg.role)
                                        put("content", msg.content)
                                    })
                                }
                            })
                            put("max_tokens", _aiMaxTokens.value)
                            put("temperature", _aiTemperature.value)
                        }
                    }

                    val writer = OutputStreamWriter(conn.outputStream)
                    writer.write(requestBody.toString())
                    writer.flush()
                    writer.close()

                    if (conn.responseCode in 200..299) {
                        val reader = BufferedReader(InputStreamReader(conn.inputStream))
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()
                        conn.disconnect()

                        // Parser response payload cleanly
                        val responseJson = JSONObject(response.toString())
                        if (isGemini) {
                            val candidates = responseJson.getJSONArray("candidates")
                            val parts = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                            parts.getJSONObject(0).getString("text")
                        } else {
                            val choices = responseJson.getJSONArray("choices")
                            choices.getJSONObject(0).getJSONObject("message").getString("content")
                        }
                    } else {
                        val errorStream = conn.errorStream
                        val errString = if (errorStream != null) {
                            BufferedReader(InputStreamReader(errorStream)).readText()
                        } else ""
                        conn.disconnect()
                        "حدث خطأ في النظام الذكي أثناء المعالجة / API Error ${conn.responseCode}: $errString"
                    }
                } catch (e: Exception) {
                    "خطأ تعذر الاتصال بالمزود / Connection Exception: ${e.localizedMessage}"
                }
            }

            // Save Assistant response
            repository.insertChatMessage(
                ChatMessage(
                    conversationId = convId,
                    role = "model",
                    content = reply
                )
            )
            repository.updateConversationTimestamp(convId, System.currentTimeMillis())
            _aiLoading.value = false
            insertLog("INFO", "AI", "تم الرد بنجاح من المساعد الذكي.")
        }
    }

    // --- Autofill accessibility simulation logger ---
    fun registerAutofillActivity(appPackage: String, detected: Int, filled: Int) {
        viewModelScope.launch {
            repository.insertAutofillAction(
                AutofillAction(
                    appPackage = appPackage,
                    fieldsDetected = detected,
                    fieldsFilled = filled
                )
            )
            insertLog("INFO", "Autofill", "تعبئة الحقول: تم ملء $filled حقل من $detected في التطبيق: $appPackage")
        }
    }

    fun clearAutofillHistory() {
        viewModelScope.launch {
            repository.clearAutofillActions()
            insertLog("WARN", "Autofill", "تم مسح سجل التعبئة التلقائية السريعة.")
        }
    }
}

class ExtensionViewModelFactory(
    private val application: Application,
    private val repository: ExtensionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExtensionViewModel::class.java)) {
            return ExtensionViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
