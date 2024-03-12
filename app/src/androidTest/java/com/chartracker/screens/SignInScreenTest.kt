package com.chartracker.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.auth.SignInScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    private val invalidCredentials =  mutableStateOf(false)
    private val emailSent = mutableStateOf(false)
    @Before
    fun setupSignInScreen(){
        composeTestRule.setContent {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            SignInScreen(
                email = email,
                onEmailType = {newInput -> email = newInput},
                password = password,
                onPasswordType = {newInput -> password = newInput},
                onSignInClick = { _, _ -> },
                onSignUpClick = { /**/ },
                signedIn = false,
                resetSignedIn = {},
                invalidCredentials = invalidCredentials.value,
                resetInvalidCredentials = {invalidCredentials.value = !invalidCredentials.value},
                navToStories = {},
                emailSent = emailSent.value,
                resetEmailSent = {emailSent.value = !emailSent.value},
                onPasswordResetClick = {}
            )
        }
    }

    @Test
    fun screenContentsTest(){
        composeTestRule
            .onNode(
                !hasClickAction()
                and
                hasText("Sign In")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Email:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                and
                hasText("Enter email")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Password:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter password")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                and
                hasText("Sign In")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Send password reset email")
            )
            .assertIsDisplayed()

    }

    @Test
    fun emailVisibleTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter email")
            )
            .performTextInput("test@email.com")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("test@email.com")
            )
            .assertIsDisplayed()
    }

    @Test
    fun passwordObscuredTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter password")
            )
            .performTextInput("password")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("••••••••")
            )
            .assertIsDisplayed()
    }

    @Test
    fun missingEmailTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter email")
            )
            .performTextInput("")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Send password reset email")
            )
            .performClick()


//        composeTestRule.onAllNodes(isRoot()).printToLog("after", maxDepth = 5)


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

    @Test
    fun missingEmailSignInTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter email")
            )
            .performTextInput("")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Missing email and/or password!")
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
                        hasText("Missing email and/or password!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun missingPasswordSignInTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter password")
            )
            .performTextInput("")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Missing email and/or password!")
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
                        hasText("Missing email and/or password!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun invalidCredentialsTest(){
        // activate the dialog directly
        invalidCredentials.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Invalid email or password!")
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
                        hasText("Invalid email or password!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun emailSentTest(){
        // activate the dialog directly
        emailSent.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Password reset email was sent to the email if it matches a user. Please wait a moment for it to arrive.")
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
                        hasText("Password reset email was sent to the email if it matches a user. Please wait a moment for it to arrive.")
            )
            .assertDoesNotExist()

    }
}