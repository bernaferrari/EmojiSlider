package com.bernaferrari.emojislider

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
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
                modifier = Modifier,
                value = latest,
                onValueChange = { latest = it },
                allowReselection = true,
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
                isUserSeekable = false,
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
                allowReselection = true,
                registerTouchOnTrack = true,
                trackInset = 0.dp,
                sliderHeight = 80.dp,
                thumbSize = 40.dp,
            )
        }
        val node = onNodeWithTag(EMOJI_SLIDER_TEST_TAG)
        node.performTouchInput {
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
                isUserSeekable = false,
                registerTouchOnTrack = true,
                trackInset = 0.dp,
            )
        }
        onNodeWithTag(EMOJI_SLIDER_TEST_TAG).performTouchInput {
            click(Offset(width * 0.9f, height / 2f))
        }
        waitForIdle()
        assertEquals(0.3f, latest, absoluteTolerance = 0.001f)
    }
}
