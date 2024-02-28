package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.auth.SettingsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupSettingsScreen(){
        val updateEmail = mutableStateOf("")
        val updatePassword = mutableStateOf("")
        val confirmedPassword = mutableStateOf("")
        composeTestRule.setContent {
            SettingsScreen(
                updatedEmail = updateEmail.value,
                onUpdatedEmailChange = {new:String -> updateEmail.value = new},
                submitUpdatedEmail = {},
                updateEmailVerificationSent = false,
                resetUpdateEmailVerificationSent = {},
                updatedPassword = updatePassword.value,
                onUpdatedPasswordChange = {new:String -> updatePassword.value = new},
                submitUpdatedPassword = {},
                confirmedPassword = confirmedPassword.value,
                onConfirmedPasswordChange = {new:String -> confirmedPassword.value = new},
                weakPassword = "",
                resetWeakPassword = {},
                invalidUser = false,
                resetInvalidUser = {},
                triggerReAuth = false,
                resetTriggerReAuth = {},
                reAuthenticateUser = {_:String ->},
                passwordUpdateSuccess = false,
                resetPasswordUpdateSuccess = {},
                signOut = { /**/ },
                readyToNavToSignIn = false,
                resetReadyToNavToSignIn = { /**/ },
                navToSignIn = { /**/ },
                deleteAccount = { /**/ },
                onBackNav = {}
            )
        }
    }

    @Test
    fun settingsScreenContentsTest(){
        composeTestRule
            .onNodeWithContentDescription("Top bar")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Update Email:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Email")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                and
                hasText("Update Email")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Update Password:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Password")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Confirm Password:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Update Password")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Out")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("DELETE ACCOUNT")
            )
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreenEmailInputTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                and
                hasText("Enter New Email")
            )
            .performTextInput("test@email.com")

        composeTestRule
            .onNodeWithText("test@email.com")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreenPasswordInputTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Password")
            )
            .performTextInput("pass123*")

        composeTestRule
            .onNodeWithText("••••••••")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Passwords do not match")
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreenPasswordMatchInputTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Password")
            )
            .performTextInput("pass123*")

        composeTestRule
            .onNodeWithText("••••••••")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .performTextInput("pass123*")

        composeTestRule
            .onNodeWithText("Passwords do not match")
            .assertDoesNotExist()
    }
}