package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.auth.SettingsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val passwordUpdateSuccess =  mutableStateOf(false)
    private val triggerReAuth =  mutableStateOf(false)
    private val weakPassword =  mutableStateOf("")
    private val invalidUser =  mutableStateOf(false)
    private val updateEmailVerificationSent =  mutableStateOf(false)

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
                updateEmailVerificationSent = updateEmailVerificationSent.value,
                resetUpdateEmailVerificationSent = {updateEmailVerificationSent.value = false},
                updatedPassword = updatePassword.value,
                onUpdatedPasswordChange = {new:String -> updatePassword.value = new},
                submitUpdatedPassword = {},
                confirmedPassword = confirmedPassword.value,
                onConfirmedPasswordChange = {new:String -> confirmedPassword.value = new},
                weakPassword = weakPassword.value,
                resetWeakPassword = {weakPassword.value = ""},
                invalidUser = invalidUser.value,
                resetInvalidUser = {invalidUser.value = false},
                triggerReAuth = triggerReAuth.value,
                resetTriggerReAuth = {triggerReAuth.value = false},
                reAuthenticateUser = {_:String ->},
                passwordUpdateSuccess = passwordUpdateSuccess.value,
                resetPasswordUpdateSuccess = {passwordUpdateSuccess.value = false},
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
    }

    @Test
    fun passwordUpdateSuccessTest(){
        // activate dialog
        passwordUpdateSuccess.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Password successfully updated!")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Password successfully updated!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun triggerReAuthTest(){
        // activate dialog
        triggerReAuth.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("This operation requires a recent sign in. Enter password to continue.")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Password:")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                hasSetTextAction()
                        and
                        hasText("Enter password")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Enter")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Cancel")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("This operation requires a recent sign in. Enter password to continue.")
            )
            .assertDoesNotExist()
    }

    @Test
    fun weakPasswordTest(){
        // activate dialog
        weakPassword.value = "Weak Password!"

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Weak Password!")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Weak Password!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun invalidUserTest(){
        // activate dialog
        invalidUser.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Error with account. Try signing in again.")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Error with account. Try signing in again.")
            )
            .assertDoesNotExist()
    }

    @Test
    fun updateEmailVerificationSentTest(){
        // activate dialog
        updateEmailVerificationSent.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("A verification email will be sent to the new email. Verify that email to complete the update.")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("A verification email will be sent to the new email. Verify that email to complete the update.")
            )
            .assertDoesNotExist()
    }

    @Test
    fun missingPasswordTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Password")
            )
            .performTextInput("pass123*")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .performTextInput("")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Update Password")
            )
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Missing password!")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Missing password!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun passwordMismatchTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Password")
            )
            .performTextInput("pass123*")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .performTextInput("pass12*")

        composeTestRule
            .onNode(
                hasClickAction()
                and
                hasText("Update Password")
            )
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Passwords do not match")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Passwords do not match")
            )
            .assertDoesNotExist()
    }

    @Test
    fun missingEmailTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Email")
            )
            .performTextInput("")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Update Email")
            )
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Missing email!")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasClickAction()
                        and
                        hasText("Dismiss")
            )
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Missing email!")
            )
            .assertDoesNotExist()
    }
}