package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WebClip::class,
        QuickNote::class,
        AppLog::class,
        QuickDataField::class,
        ChatConversation::class,
        ChatMessage::class,
        AutofillAction::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun webClipDao(): WebClipDao
    abstract fun quickNoteDao(): QuickNoteDao
    abstract fun appLogDao(): AppLogDao
    abstract fun quickDataFieldDao(): QuickDataFieldDao
    abstract fun chatConversationDao(): ChatConversationDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun autofillActionDao(): AutofillActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "web_companion_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
