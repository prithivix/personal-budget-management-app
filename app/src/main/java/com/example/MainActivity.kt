package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TallyBg
import com.example.ui.viewmodel.TallyViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: TallyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = TallyBg
        ) {
          val uiState by viewModel.uiState.collectAsStateWithLifecycle()
          MainAppScreen(
            uiState = uiState,
            viewModel = viewModel
          )
        }
      }
    }
  }
}

