package com.waqas028.ktor_upload_file_with_progress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.waqas028.ktor_upload_file_with_progress.ui.theme.KtorUploadFileWithProgressTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KtorUploadFileWithProgressTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UploadFileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}