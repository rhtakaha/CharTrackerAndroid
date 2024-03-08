package com.chartracker.components

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.components.MessageDialog
import org.junit.Rule
import org.junit.Test
import com.chartracker.R
import com.chartracker.ui.components.ReAuthDialog
import com.chartracker.ui.components.RefreshDialog

class DialogsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun messageDialogTest(){
        composeTestRule.setContent {
            MessageDialog(message = stringResource(id = R.string.invalid_email_password)) {
            }
        }

        composeTestRule
            .onNodeWithText("Invalid email or password!")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Dismiss")
            .assertHasClickAction()
            .assertIsDisplayed()
    }

    @Test
    fun refreshDialogTest(){
        composeTestRule.setContent {
            RefreshDialog(message = "Failed to retrieve characters.", refresh = { /**/ }) {

            }
        }

        composeTestRule
            .onNodeWithText("Failed to retrieve characters.")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Refresh")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Dismiss")
            .assertHasClickAction()
            .assertIsDisplayed()
    }

    @Test
    fun reAuthDialogTest(){
        composeTestRule.setContent {
            ReAuthDialog(
                confirmReauthentication ={},
                onDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithText("Enter password")
            .performTextInput("pass123")

        composeTestRule
            .onNodeWithText("•••••••")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("This operation requires a recent sign in. Enter password to continue.")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Password:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cancel")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Enter")
            .assertHasClickAction()
            .assertIsDisplayed()
    }
}