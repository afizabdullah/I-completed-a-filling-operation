package com.example.data

import kotlinx.coroutines.flow.Flow

class ExtensionRepository(
    private val webClipDao: WebClipDao,
    private val quickNoteDao: QuickNoteDao,
    private val appLogDao: AppLogDao,
    private val quickDataFieldDao: QuickDataFieldDao,
    private val chatConversationDao: ChatConversationDao,
    private val chatMessageDao: ChatMessageDao,
    private val autofillActionDao: AutofillActionDao
) {
    val allClips: Flow<List<WebClip>> = webClipDao.getAllClips()
    val allNotes: Flow<List<QuickNote>> = quickNoteDao.getAllNotes()
    val allLogs: Flow<List<AppLog>> = appLogDao.getAllLogs()
    val allQuickFields: Flow<List<QuickDataField>> = quickDataFieldDao.getAllFields()
    val allConversations: Flow<List<ChatConversation>> = chatConversationDao.getAllConversations()
    val allAutofillActions: Flow<List<AutofillAction>> = autofillActionDao.getAllActions()

    fun getMessagesForConversation(convId: Int): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForConversation(convId)
    }

    // Quick fields helpers
    suspend fun insertQuickField(field: QuickDataField) {
        quickDataFieldDao.insertField(field)
    }

    suspend fun insertQuickFields(fields: List<QuickDataField>) {
        quickDataFieldDao.insertAll(fields)
    }

    suspend fun clearQuickFields() {
        quickDataFieldDao.clearAllFields()
    }

    suspend fun deleteQuickFieldById(id: Int) {
        quickDataFieldDao.deleteFieldById(id)
    }

    // Conversations helpers
    suspend fun insertConversation(conversation: ChatConversation): Long {
        return chatConversationDao.insertConversation(conversation)
    }

    suspend fun deleteConversationById(id: Int) {
        chatConversationDao.deleteConversationById(id)
    }

    suspend fun updateConversationTimestamp(id: Int, timestamp: Long) {
        chatConversationDao.updateTimestamp(id, timestamp)
    }

    // Message helpers
    suspend fun insertChatMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun deleteMessagesForConversation(convId: Int) {
        chatMessageDao.deleteMessagesForConversation(convId)
    }

    // Autofill actions helpers
    suspend fun insertAutofillAction(action: AutofillAction) {
        autofillActionDao.insertAction(action)
    }

    suspend fun clearAutofillActions() {
        autofillActionDao.clearAllActions()
    }

    fun getClipsByCategory(category: String): Flow<List<WebClip>> {
        return webClipDao.getClipsByCategory(category)
    }

    suspend fun insertLog(log: AppLog) {
        appLogDao.insertLog(log)
    }

    suspend fun clearLogs() {
        appLogDao.clearAllLogs()
    }


    suspend fun insertClip(clip: WebClip) {
        webClipDao.insertClip(clip)
    }

    suspend fun updateClip(clip: WebClip) {
        webClipDao.updateClip(clip)
    }

    suspend fun deleteClip(clip: WebClip) {
        webClipDao.deleteClip(clip)
    }

    suspend fun deleteClipById(id: Int) {
        webClipDao.deleteClipById(id)
    }

    suspend fun insertNote(note: QuickNote) {
        quickNoteDao.insertNote(note)
    }

    suspend fun updateNote(note: QuickNote) {
        quickNoteDao.updateNote(note)
    }

    suspend fun deleteNote(note: QuickNote) {
        quickNoteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Int) {
        quickNoteDao.deleteNoteById(id)
    }
}
