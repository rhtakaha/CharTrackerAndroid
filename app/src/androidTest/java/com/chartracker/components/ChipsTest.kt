package com.chartracker.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.chartracker.ui.components.BasicChip
import com.chartracker.ui.components.ChipGroupRow
import org.junit.Rule
import org.junit.Test

class ChipsTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun basicChipContentTest(){
        composeTestRule.setContent {
            BasicChip(text = "AChip", onClick = {})
        }

        composeTestRule
            .onNodeWithText("AChip")
            .assertIsDisplayed()
    }

    @Test
    fun basicChipPressTest(){
        composeTestRule.setContent {
            BasicChip(text = "AChip", onClick = {})
        }

        composeTestRule
            .onNodeWithText("AChip")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("AChip")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()
    }

    @Test
    fun basicChipUnpressTest(){
        composeTestRule.setContent {
            BasicChip(text = "AChip", onClick = {})
        }

        composeTestRule
            .onNodeWithText("AChip")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("AChip")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("AChip")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertDoesNotExist()
    }

    @Test
    fun chipGroupContentsTest(){
        composeTestRule.setContent {
            ChipGroupRow(
                header = "Allies",
                contentsList = listOf("Aragorn", "Gandalf", "Merry", "Pippin", "Sam", "Frodo"),
                onClick = { _, _ ->}
            )
        }

        composeTestRule
            .onNodeWithText("Allies")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aragorn")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gandalf")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Merry")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pippin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sam")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Frodo")
            .assertIsDisplayed()
    }

    @Test
    fun chipGroupSelectableTest(){
        composeTestRule.setContent {
            ChipGroupRow(
                header = "Allies",
                contentsList = listOf("Aragorn", "Gandalf", "Merry", "Pippin", "Sam", "Frodo"),
                onClick = { _, _ ->}
            )
        }

        composeTestRule
            .onNodeWithText("Allies")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aragorn")
            .performClick()

        composeTestRule
            .onNodeWithText("Aragorn")
            .assertIsSelected()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gandalf")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Merry")
            .performClick()

        composeTestRule
            .onNodeWithText("Merry")
            .assertIsSelected()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pippin")
            .performClick()

        composeTestRule
            .onNodeWithText("Pippin")
            .assertIsSelected()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pippin")
            .performClick()

        composeTestRule
            .onNodeWithText("Pippin")
            .assertIsNotSelected()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sam")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Frodo")
            .assertIsNotDisplayed()
    }

    @Test
    fun chipGroupScrollTest(){
        composeTestRule.setContent {
            ChipGroupRow(
                header = "Allies",
                contentsList = listOf("Aragorn", "Gandalf", "Merry", "Pippin", "Sam", "Frodo", "Gollum", "Farimir", "Borimir"),
                onClick = { _, _ ->}
            )
        }

        composeTestRule
            .onNodeWithText("Allies")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aragorn")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gandalf")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Merry")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pippin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sam")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Frodo")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Borimir")
            .performScrollTo()

        composeTestRule
            .onNodeWithText("Gollum")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Farimir")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Borimir")
            .assertIsDisplayed()
    }

}