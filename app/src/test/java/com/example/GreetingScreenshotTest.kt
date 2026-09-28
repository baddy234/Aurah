package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun library_screenshot() {
    val sampleTrack = Track("1", "Wake Up", "The Kyoto Connection", "http://example.com/1.mp3", null, "FF6200EE")
    composeTestRule.setContent {
      MyApplicationTheme {
        TrackListItem(
          track = sampleTrack,
          displayTitle = "Wake Up",
          displayArtist = "The Kyoto Connection",
          isPlaying = true,
          isFavorite = true,
          onClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/library_item.png")
  }
}
