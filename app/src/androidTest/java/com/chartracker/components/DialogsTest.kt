package com.chartracker.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.chartracker.ui.components.MessageDialog
import org.junit.Rule
import org.junit.Test
import com.chartracker.R

class DialogsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun messageDialogTest(){
        composeTestRule.setContent {
            MessageDialog(message = R.string.invalid_email_password) {
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
}