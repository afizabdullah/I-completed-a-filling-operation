package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.AppDatabase
import com.example.data.AppLog
import com.example.data.AutofillAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject

class WebShieldAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var database: AppDatabase? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        database = AppDatabase.getDatabase(applicationContext)
        logToSystem("خدمة التعبئة التلقائية تم ربطها وتفعيلها بنجاح.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return
        val packageName = event?.packageName?.toString() ?: "unknown"

        // Avoid infinite loop on our own app pages
        if (packageName == "com.example" || packageName.startsWith("com.aistudio")) {
            return
        }

        serviceScope.launch {
            // Retrieve key-value map from SharedPreferences
            val prefs = getSharedPreferences("web_companion_prefs", Context.MODE_PRIVATE)
            val rawJson = prefs.getString("quick_autofill_data", "") ?: ""
            if (rawJson.isEmpty()) return@launch

            val dataObj = JSONObject(rawJson)
            val autofillMap = mutableMapOf<String, String>()
            val keys = dataObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                autofillMap[key] = dataObj.getString(key)
            }

            // Let's analyze the current active window for nodes
            val textNodes = mutableListOf<AccessibilityNodeInfo>()
            findInputFields(rootNode, textNodes)

            if (textNodes.isEmpty()) return@launch

            var fieldsDetected = textNodes.size
            var fieldsFilled = 0

            for (node in textNodes) {
                val hint = (node.hintText ?: "").toString().lowercase()
                val text = (node.text ?: "").toString().lowercase()
                val contentDesc = (node.contentDescription ?: "").toString().lowercase()
                val viewId = (node.viewIdResourceName ?: "").toString().lowercase()

                var valueToFill: String? = null

                // Walk over key combinations matching categories
                for ((key, value) in autofillMap) {
                    val normalizedKey = key.lowercase()
                    if (hint.contains(normalizedKey) || 
                        contentDesc.contains(normalizedKey) || 
                        viewId.contains(normalizedKey) ||
                        (normalizedKey == "name" && (hint.contains("name") || hint.contains("اسم"))) ||
                        (normalizedKey == "email" && (hint.contains("email") || hint.contains("بريد") || hint.contains("mail"))) ||
                        (normalizedKey == "phone" && (hint.contains("phone") || hint.contains("هاتف") || hint.contains("جوال") || hint.contains("موبايل") || hint.contains("tel"))) ||
                        (normalizedKey == "address" && (hint.contains("address") || hint.contains("عنوان") || hint.contains("شارع") || hint.contains("دولة") || hint.contains("بلد")))
                    ) {
                        valueToFill = value
                        break
                    }
                }

                // Generic category heuristics if direct match wasn't found
                if (valueToFill == null) {
                    valueToFill = when {
                        hint.contains("اسم") || hint.contains("name") || viewId.contains("name") -> {
                            findValueByKeywords(autofillMap, listOf("الاسم", "name", "Full Name"))
                        }
                        hint.contains("بريد") || hint.contains("email") || hint.contains("mail") || viewId.contains("email") -> {
                            findValueByKeywords(autofillMap, listOf("البريد", "email", "Email Address", "mail"))
                        }
                        hint.contains("هاتف") || hint.contains("جوال") || hint.contains("موبايل") || hint.contains("phone") || hint.contains("mobile") || viewId.contains("phone") -> {
                            findValueByKeywords(autofillMap, listOf("الهاتف", "جوال", "phone", "mobile", "tel"))
                        }
                        hint.contains("عنوان") || hint.contains("address") || hint.contains("دولة") || hint.contains("بلد") || hint.contains("مدينة") || viewId.contains("address") -> {
                            findValueByKeywords(autofillMap, listOf("العنوان", "address", "بلد", "دولة", "مدينة"))
                        }
                        else -> null
                    }
                }

                if (valueToFill != null && (node.text == null || node.text.isEmpty())) {
                    val arguments = Bundle()
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, valueToFill)
                    val success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    if (success) {
                        fieldsFilled++
                    }
                }
            }

            // Click submit / next button if fields filled greater than 0
            if (fieldsFilled > 0) {
                clickSubmissionButton(rootNode)

                // Save stats into DB
                database?.autofillActionDao()?.insertAction(
                    AutofillAction(
                        appPackage = packageName,
                        fieldsDetected = fieldsDetected,
                        fieldsFilled = fieldsFilled
                    )
                )

                database?.appLogDao()?.insertLog(
                    AppLog(
                        level = "INFO",
                        tag = "Autofill",
                        message = "تمت تعبئة تلقائية لـ $fieldsFilled حقل في تطبيق: $packageName والضغط على زر المتابعة بنجاح."
                    )
                )
            }
        }
    }

    private fun findInputFields(node: AccessibilityNodeInfo, textNodes: MutableList<AccessibilityNodeInfo>) {
        if (node.className != null && node.className.toString().contains("EditText", ignoreCase = true)) {
            textNodes.add(node)
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findInputFields(child, textNodes)
            }
        }
    }

    private fun findValueByKeywords(map: Map<String, String>, keywords: List<String>): String? {
        for (kw in keywords) {
            for ((key, value) in map) {
                if (key.contains(kw, ignoreCase = true)) {
                    return value
                }
            }
        }
        return null
    }

    private fun clickSubmissionButton(node: AccessibilityNodeInfo): Boolean {
        // Look search for buttons with action keywords
        if (node.className != null && (node.className.toString().contains("Button", ignoreCase = true) || node.isClickable)) {
            val text = (node.text ?: "").toString().lowercase()
            val contentDesc = (node.contentDescription ?: "").toString().lowercase()
            val viewId = (node.viewIdResourceName ?: "").toString().lowercase()

            val triggers = listOf(
                "ارسل", "إرسال", "حفظ", "متابعة", "موافقة", "دخول", "تسجيل", "التالي",
                "send", "submit", "save", "next", "continue", "login", "register", "confirm", "go"
            )

            for (trig in triggers) {
                if (text.contains(trig) || contentDesc.contains(trig) || viewId.contains(trig)) {
                    val clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (clicked) return true
                }
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                val found = clickSubmissionButton(child)
                if (found) return true
            }
        }
        return false
    }

    override fun onInterrupt() {}

    private fun logToSystem(msg: String) {
        serviceScope.launch {
            database?.appLogDao()?.insertLog(
                AppLog(level = "INFO", tag = "Autofill", message = msg)
            )
        }
    }
}
