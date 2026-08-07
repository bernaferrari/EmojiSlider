package com.bernaferrari.emojislider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class EmojiSliderSemanticsTest {

    @Test
    fun setProgress_updatesValueWhenSeekable() = runComposeUiTest {
        var latest = 0.25f
        setContent {
            EmojiSlider(
                value = latest,
                onValueChange = { latest = it },
                behavior = EmojiSliderBehavior(allowReselection = true),
            )
        }
        onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.8f) }
        waitForIdle()
        assertEquals(0.8f, latest, absoluteTolerance = 0.001f)
    }

    @Test
    fun setProgress_isIgnoredWhenNotSeekable() = runComposeUiTest {
        var latest = 0.4f
        setContent {
            EmojiSlider(
                value = latest,
                onValueChange = { latest = it },
                behavior = EmojiSliderBehavior(isUserSeekable = false),
            )
        }
        onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.9f) }
        waitForIdle()
        assertEquals(0.4f, latest, absoluteTolerance = 0.001f)
    }

    @Test
    fun tapOnTrack_updatesValueWhenRegisterTouchOnTrack() = runComposeUiTest {
        var latest = 0.1f
        setContent {
            EmojiSlider(
                value = latest,
                onValueChange = { latest = it },
                behavior = EmojiSliderBehavior(allowReselection = true, registerTouchOnTrack = true),
                sizes = EmojiSliderSizes(trackInset = 0.dp, sliderHeight = 80.dp, thumbSize = 40.dp),
            )
        }
        onNodeWithTag(EMOJI_SLIDER_TEST_TAG).performTouchInput {
            click(Offset(width * 0.8f, height / 2f))
        }
        waitForIdle()
        assertTrue(latest > 0.5f, "expected tap toward the right end, got $latest")
    }

    @Test
    fun tap_doesNotUpdateWhenNotSeekable() = runComposeUiTest {
        var latest = 0.3f
        setContent {
            EmojiSlider(
                value = latest,
                onValueChange = { latest = it },
                behavior = EmojiSliderBehavior(isUserSeekable = false, registerTouchOnTrack = true),
                sizes = EmojiSliderSizes(trackInset = 0.dp),
            )
        }
        onNodeWithTag(EMOJI_SLIDER_TEST_TAG).performTouchInput {
            click(Offset(width * 0.9f, height / 2f))
        }
        waitForIdle()
        assertEquals(0.3f, latest, absoluteTolerance = 0.001f)
    }

    @Test
    fun drag_updatesValueAlongTrack() = runComposeUiTest {
        var latest = 0.15f
        setContent {
            EmojiSlider(
                value = latest,
                onValueChange = { latest = it },
                behavior = EmojiSliderBehavior(allowReselection = true, registerTouchOnTrack = true),
                sizes = EmojiSliderSizes(trackInset = 0.dp, sliderHeight = 80.dp, thumbSize = 40.dp),
            )
        }
        onNodeWithTag(EMOJI_SLIDER_TEST_TAG).performTouchInput {
            down(Offset(width * 0.2f, height / 2f))
            moveTo(Offset(width * 0.85f, height / 2f))
            up()
        }
        waitForIdle()
        assertTrue(latest > 0.6f, "expected drag toward the right end, got $latest")
    }

    @Test
    fun oneShotTap_locksAndShowsAverageTooltip() = runComposeUiTest {
        var latest = 0.2f
        setContent {
            EmojiSlider(
                value = latest,
                onValueChange = { latest = it },
                behavior = EmojiSliderBehavior(
                    allowReselection = false,
                    displayAverage = true,
                    displayTooltip = true,
                    registerTouchOnTrack = true,
                ),
                sizes = EmojiSliderSizes(trackInset = 0.dp, sliderHeight = 80.dp, thumbSize = 40.dp),
            )
        }
        onNodeWithTag(EMOJI_SLIDER_TEST_TAG).performTouchInput {
            click(Offset(width * 0.8f, height / 2f))
        }
        waitForIdle()
        mainClock.advanceTimeBy(TAP_RELEASE_PARTICLE_DELAY_MILLIS + 50)
        waitForIdle()

        assertTrue(latest > 0.5f, "expected tap to move value, got $latest")
        onNodeWithTag(EMOJI_SLIDER_TOOLTIP_TEST_TAG).assertIsDisplayed()

        val locked = latest
        onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.05f) }
        waitForIdle()
        assertEquals(locked, latest, absoluteTolerance = 0.001f)
    }
}
