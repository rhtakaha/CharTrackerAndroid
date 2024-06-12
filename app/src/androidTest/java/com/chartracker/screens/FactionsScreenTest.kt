package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.chartracker.ui.story.FactionsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FactionsScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    private val failedGetFactions = mutableStateOf(false)
    private val duplicateNameError = mutableStateOf(false)
    private val failedUpdateFactions = mutableStateOf(false)

    @Before
    fun setupFactionsScreen(){
        composeTestRule.setContent {
            val factions = mapOf(
                "Straw Hat Pirates" to 0xFF0000FF,
                "Silver Fox Pirates" to 0xff949494,
                "World Government" to 0xffa4ffa4,
            )

            FactionsScreen(
                factions = factions,
                failedGetFactions = failedGetFactions.value,
                resetFailedGetFactions = { failedGetFactions.value = false },
                duplicateNameError = duplicateNameError.value,
                resetDuplicateNameError = { duplicateNameError.value = false },
                failedUpdateFactions = failedUpdateFactions.value,
                resetFailedUpdateFactions = { failedUpdateFactions.value = false },
                refreshFactions = {  },
                onCreate = {},
                onUpdate = {_, _, _ ->},
                onDelete = {},
                submitFactions = {  },
                storyTitle = "One Piece"
            ) {

            }
        }
    }

    @Test
    fun factionsContentTest(){
        //Top bar
        composeTestRule
            .onNodeWithText("One Piece")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Edit factions")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()
            .assertHasClickAction()

        // add
        composeTestRule
            .onNode(
                hasText("Enter faction")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Add")
                        and
                        hasClickAction()
            )
            .assertIsDisplayed()

        //factions
        composeTestRule
            .onNodeWithText("Straw Hat Pirates")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Silver Fox Pirates")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("World Government")
            .assertIsDisplayed()
    }

    @Test
    fun factionsContentTestEditing(){
        //Top bar
        composeTestRule
            .onNodeWithText("One Piece")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Edit factions")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Edit factions")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Submit factions changes")
            .assertIsDisplayed()
            .assertHasClickAction()

        // add
        composeTestRule
            .onNode(
                hasText("")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Add")
                        and
                        hasClickAction()
            )
            .assertIsDisplayed()

        //factions
        composeTestRule
            .onNode(
                hasText("Straw Hat Pirates")
                and
                hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Silver Fox Pirates")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("World Government")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()
    }

    @Test
    fun failedGetFactionsTest(){
        failedGetFactions.value = true

        composeTestRule
            .onNodeWithText("Issue getting factions. Refresh to try again.")
            .assertIsDisplayed()
    }

    @Test
    fun failedUpdateFactionsTest(){
        failedUpdateFactions.value = true

        composeTestRule
            .onNodeWithText("Issue updating factions.")
            .assertIsDisplayed()
    }

    @Test
    fun duplicateNameErrorTest(){
        duplicateNameError.value = true

        composeTestRule
            .onNodeWithText("Duplicate faction! Ensure each faction in each story is unique.")
            .assertIsDisplayed()
    }

    @Test
    fun confirmBackTest(){
        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithText("Are you sure you want to go back? All current progress will be lost?")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun confirmSubmitTest(){
        composeTestRule
            .onNodeWithContentDescription("Edit factions")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Submit factions changes")
            .performClick()

        composeTestRule
            .onNodeWithText("Are you sure you want to submit all changes? All changes are irreversible!")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}