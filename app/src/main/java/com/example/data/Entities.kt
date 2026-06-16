package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "web_clips")
data class WebClip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val title: String,
    val note: String,
    val category: String, // e.g., "General", "Read Later", "Work", "Shopping"
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "quick_notes")
data class QuickNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "app_logs")
data class AppLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val level: String, // "INFO", "WARN", "DANGER", "BLOCK"
    val tag: String,   // E.g., "AdBlock", "System", "Overlay", "Vault"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "quick_data_fields")
data class QuickDataField(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,
    val value: String,
    val category: String, // e.g. "name", "email", "phone", "address", "generic"
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "chat_conversations")
data class ChatConversation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val lastUpdated: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: Int,
    val role: String, // "user", "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "autofill_actions")
data class AutofillAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appPackage: String,
    val fieldsDetected: Int,
    val fieldsFilled: Int,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable


