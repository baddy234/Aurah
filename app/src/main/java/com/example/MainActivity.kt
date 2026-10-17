package com.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: MediaPlayerViewModel by viewModels()

  @android.annotation.SuppressLint("InvalidFragmentVersionForActivityResult")
  private val requestPermissionsLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) {}

  override fun onUserLeaveHint() {
    super.onUserLeaveHint()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && viewModel.state.value.currentTrack?.isVideo == true && viewModel.state.value.isPlaying) {
      try {
        val params = android.app.PictureInPictureParams.Builder().build()
        enterPictureInPictureMode(params)
      } catch (e: Exception) {
        // Ignore if PiP not supported
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val permissionsToRequest = mutableListOf<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
      permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
      permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
    } else {
      permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())

    val incomingMediaUri = if (intent?.action == Intent.ACTION_VIEW) {
      intent.data
    } else {
      null
    }

    handleIncomingIntent(intent)
    
    setContent {
      val state by viewModel.state.collectAsState()
      val systemInDark = androidx.compose.foundation.isSystemInDarkTheme()
      val isDark = when (state.themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> systemInDark
      }
      MyApplicationTheme(
        darkTheme = isDark,
        dynamicColor = state.dynamicColor,
        accentColorIndex = state.accentColorIndex
      ) {
        MediaPlayerApp(
          viewModel = viewModel,
          incomingUri = incomingMediaUri
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIncomingIntent(intent)
  }

  private fun handleIncomingIntent(intent: Intent?) {
    if (intent == null) return
    val isOpenPlayerAction = intent.action == PlaybackService.ACTION_OPEN_PLAYER ||
      intent.getBooleanExtra(PlaybackService.EXTRA_OPEN_AUDIO_PLAYER, false)
    if (isOpenPlayerAction) {
      viewModel.openAudioPlayer()
      setIntent(Intent())
    }
  }
}


