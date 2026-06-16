package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.ExtensionRepository
import com.example.ui.ExtensionViewModel
import com.example.ui.ExtensionViewModelFactory
import com.example.ui.MainScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Core Room Database, Repository, and ViewModel dependencies
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ExtensionRepository(
            database.webClipDao(),
            database.quickNoteDao(),
            database.appLogDao(),
            database.quickDataFieldDao(),
            database.chatConversationDao(),
            database.chatMessageDao(),
            database.autofillActionDao()
        )
        val viewModel = ViewModelProvider(
            this,
            ExtensionViewModelFactory(application, repository)
        )[ExtensionViewModel::class.java]

        enableEdgeToEdge()
        
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
