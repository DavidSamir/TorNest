package com.torfilx.core.ui.component

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.dp
import com.torfilx.core.model.MediaCard
import com.torfilx.core.model.MediaItem
import com.torfilx.core.ui.theme.TorfilxColors
import com.torfilx.core.ui.theme.TorfilxTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Checks the behavior most at risk when changing a TV design: focus, activation, and hit geometry. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@OptIn(ExperimentalTestApi::class)
class TvDesignTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Before fun useRemoteInputBeforeComposition() {
        compose.runOnUiThread { compose.activity.window.decorView.requestFocusFromTouch() }
    }

    private val cards = listOf("Northshore", "The Last Light", "Wild Places", "A New Horizon", "Stillwater")
        .mapIndexed { index, title -> MediaCard(MediaItem(id = "preview-$index", title = title,
            year = 2026, overview = "Discover a different perspective. A journey worth taking, from the comfort of your living room.",
            genres = listOf("Adventure", "Drama"))) }

    @Test fun navigationChangesOnlyWhenActivated() {
        compose.setContent {
            var selected by remember { mutableStateOf("Home") }
            TvTestTheme(reduceMotion = true) {
                Column(Modifier.fillMaxSize().background(TorfilxColors.Background)) {
                    TopNavBar(listOf("Home", "Movies", "Shows", "My List", "Search", "Settings")
                        .map { TopNavItem(it, it) }, selected, { selected = it.id })
                    HeroSection(cards.take(2), { "Play" }, {}, {}, {})
                }
            }
        }
        val movies = compose.onNodeWithText("Movies")
        movies.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        movies.assertIsFocused().assertIsNotSelected()
        compose.onNodeWithText("Home").assertIsSelected()
        movies.performKeyInput { pressKey(Key.DirectionCenter) }
        movies.assertIsSelected()
        screenshot("home")
    }

    @Test fun remoteMovesBetweenButtonsAndActivatesOnlyFocusedAction() {
        var played = 0
        var details = 0
        compose.setContent {
            TvTestTheme {
                Row(Modifier.padding(48.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    TvButton("Play", { played++ }, autoFocus = true)
                    TvButton("Details", { details++ }, primary = false)
                }
            }
        }
        compose.onNodeWithText("Play").assertIsFocused().performKeyInput { pressKey(Key.DirectionRight) }
        compose.onNodeWithText("Details").assertIsFocused().performKeyInput { pressKey(Key.DirectionCenter) }
        compose.runOnIdle { assertEquals(0, played); assertEquals(1, details) }
    }

    @Test fun focusAnimationDoesNotMoveTheCardsNavigationBounds() {
        compose.setContent {
            TvTestTheme {
                MediaRow("Continue exploring", cards, {})
            }
        }
        val card = compose.onNodeWithContentDescription("Northshore, 2026")
        val before = card.fetchSemanticsNode().boundsInRoot
        card.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        compose.mainClock.advanceTimeBy(250)
        card.assertIsFocused()
        assertEquals(before, card.fetchSemanticsNode().boundsInRoot)
        screenshot("cards")
    }

    @Test fun keyboardEntryAndClearingStayFunctional() {
        compose.setContent {
            var query by remember { mutableStateOf("") }
            TvTestTheme(reduceMotion = true) {
                Column(Modifier.fillMaxSize().background(TorfilxColors.Background).padding(48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SearchField(query, "Search films and shows", Modifier.padding(end = 400.dp))
                    OnScreenKeyboard({ query += it }, { query = query.dropLast(1) }, { query = "" })
                }
            }
        }
        compose.onNodeWithText("A").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        compose.onNodeWithText("A").performKeyInput { pressKey(Key.DirectionCenter) }
        compose.onNodeWithContentDescription("Search: A").assertExists()
        compose.onNodeWithText("clear").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        compose.onNodeWithText("clear").performKeyInput { pressKey(Key.DirectionCenter) }
        compose.onNodeWithContentDescription("Search films and shows").assertExists()
        screenshot("search")
    }

    @Test fun actionMenuKeepsFocusInsideTheModal() {
        compose.setContent {
            TvTestTheme(reduceMotion = true) {
                Column(Modifier.fillMaxSize().background(TorfilxColors.Background)) {
                    TvButton("Background action", {})
                    ActionMenu("Episode options", listOf(MenuAction("Play episode", {})), {})
                }
            }
        }
        compose.onNodeWithText("Play episode").assertIsFocused()
            .performKeyInput { pressKey(Key.DirectionDown) }
        compose.onNodeWithText("Cancel").assertIsFocused()
            .performKeyInput { pressKey(Key.DirectionDown) }
        compose.onNodeWithText("Cancel").assertIsFocused()
        screenshot("menu")
    }

    @Test fun sharingConsentKeepsRemoteFocusInsideItsChoices() {
        compose.setContent {
            TvTestTheme(reduceMotion = true) {
                SharingConsentDialog("Storage and sharing can be adjusted in Settings.", {}, {})
            }
        }
        compose.onNodeWithText("Enable sharing").assertIsFocused()
            .performKeyInput { pressKey(Key.DirectionRight) }
        compose.onNodeWithText("Not now").assertIsFocused()
            .performKeyInput { pressKey(Key.DirectionRight) }
        compose.onNodeWithText("Not now").assertIsFocused()
        screenshot("consent")
    }

    @Test fun featuredTitlesAdvanceOnlyOnRequestAndKeepTheControlFocused() {
        compose.setContent {
            TvTestTheme { HeroSection(cards.take(2), { "Play" }, {}, {}, {}) }
        }
        compose.mainClock.advanceTimeBy(9_000)
        compose.onNodeWithText("Northshore").assertExists()
        val next = compose.onNodeWithText("Next feature")
        next.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        next.performKeyInput { pressKey(Key.DirectionCenter) }
        compose.onNodeWithText("The Last Light").assertExists()
        next.assertIsFocused()
    }

    private fun screenshot(name: String) {
        val directory = File("build/ui-previews").apply { mkdirs() }
        // Draw the real Android view through Robolectric's native renderer. PixelCopy waits for a
        // display frame that a headless JVM does not produce reliably on Windows.
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            File(directory, "$name.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            bitmap.recycle()
        }
    }

    @Composable
    private fun TvTestTheme(reduceMotion: Boolean = false, content: @Composable () -> Unit) {
        val input = LocalInputModeManager.current
        // Robolectric starts in touch mode. TV focusable controls require remote/keyboard mode.
        SideEffect { input.requestInputMode(InputMode.Keyboard) }
        TorfilxTheme(reduceMotion = reduceMotion) {
            Box(Modifier.fillMaxSize().background(TorfilxColors.Background)) { content() }
        }
    }
}
