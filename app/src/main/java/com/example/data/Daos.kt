package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WebClipDao {
    @Query("SELECT * FROM web_clips ORDER BY timestamp DESC")
    fun getAllClips(): Flow<List<WebClip>>

    @Query("SELECT * FROM web_clips WHERE category = :category ORDER BY timestamp DESC")
    fun getClipsByCategory(category: String): Flow<List<WebClip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: WebClip)

    @Update
    suspend fun updateClip(clip: WebClip)

    @Delete
    suspend fun deleteClip(clip: WebClip)

    @Query("DELETE FROM web_clips WHERE id = :id")
    suspend fun deleteClipById(id: Int)
}

@Dao
interface QuickNoteDao {
    @Query("SELECT * FROM quick_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<QuickNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: QuickNote)

    @Update
    suspend fun updateNote(note: QuickNote)

    @Delete
    suspend fun deleteNote(note: QuickNote)

    @Query("DELETE FROM quick_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)
}

@Dao
interface AppLogDao {
    @Query("SELECT * FROM app_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AppLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AppLog)

    @Query("DELETE FROM app_logs")
    suspend fun clearAllLogs()
}

@Dao
interface QuickDataFieldDao {
    @Query("SELECT * FROM quick_data_fields ORDER BY timestamp DESC")
    fun getAllFields(): Flow<List<QuickDataField>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: QuickDataField)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fields: List<QuickDataField>)

    @Query("DELETE FROM quick_data_fields")
    suspend fun clearAllFields()

    @Query("DELETE FROM quick_data_fields WHERE id = :id")
    suspend fun deleteFieldById(id: Int)
}

@Dao
interface ChatConversationDao {
    @Query("SELECT * FROM chat_conversations ORDER BY lastUpdated DESC")
    fun getAllConversations(): Flow<List<ChatConversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversation): Long

    @Query("DELETE FROM chat_conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Int)

    @Query("UPDATE chat_conversations SET lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: Int, timestamp: Long)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE conversationId = :convId")
    suspend fun deleteMessagesForConversation(convId: Int)
}

@Dao
interface AutofillActionDao {
    @Query("SELECT * FROM autofill_actions ORDER BY timestamp DESC")
    fun getAllActions(): Flow<List<AutofillAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: AutofillAction)

    @Query("DELETE FROM autofill_actions")
    suspend fun clearAllActions()
}


