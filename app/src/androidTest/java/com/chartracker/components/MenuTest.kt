package com.chartracker.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.chartracker.ui.components.SortingMenu
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MenuTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupMenu(){
        composeTestRule.setContent {
            SortingMenu(
                alphaSort = { /**/ },
                reverseAlphaSort = { /**/ },
                recentSort = { /**/ },
                reverseRecentSort = {})
        }
    }

    @Test
    fun sortingMenuContentTest(){
        composeTestRule.onNode(
            hasClickAction()
            and
            hasContentDescription("Opens menu for sorting options")
        )
            .performClick()

        composeTestRule
            .onNodeWithText("Alphabetical")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Reverse Alphabetical")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Most Recent")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Least Recent")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Least Recent")
            .performClick()

        composeTestRule
            .onNodeWithText("Alphabetical")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Reverse Alphabetical")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Most Recent")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("Least Recent")
            .assertDoesNotExist()
    }
}