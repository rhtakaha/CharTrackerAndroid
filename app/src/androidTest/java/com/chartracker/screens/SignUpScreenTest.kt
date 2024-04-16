package com.chartracker.screens

import com.chartracker.R
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
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.auth.SignUpScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val signUpErrorMessage = mutableStateOf<Any?>(null)
    @Before
    fun setupSignUpScreen(){
        composeTestRule.setContent {
            var email by remember { mutableStateOf("") }
            var password1 by remember { mutableStateOf("") }
            var password2 by remember { mutableStateOf("") }

            SignUpScreen(
                email = email,
                onEmailType = {newInput -> email = newInput} ,
                password1 = password1,
                onPassword1Type = {newInput -> password1 = newInput} ,
                password2 = password2,
                onPassword2Type = {newInput -> password2 = newInput} ,
                onSignUpClick = { _, _ ->},
                signedIn = false,
                resetSignedIn = {},
                signUpErrorMessage = signUpErrorMessage.value,
                resetSignUpErrorMessage = {signUpErrorMessage.value  = null},
                navToVerifyEmail = {},
                onBackNav = {}
            )
        }
    }

    @Test
    fun screenContentsTest(){
        composeTestRule
            .onNodeWithContentDescription("Top bar")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                !hasClickAction()
                and
                hasText("Sign Up")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Email:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Email")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Password:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Password")
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
                    hasText("Sign Up")
            )
            .assertIsDisplayed()
    }

    @Test
    fun emailVisibleTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Email")
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
                        hasText("Enter Password")
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
    fun confirmPasswordObscuredTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
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
    fun passwordMismatchTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Email")
            )
            .performTextInput("test@email.com")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Password")
            )
            .performTextInput("password")


        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .performTextInput("passwod")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
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
                        hasText("Enter Password")
            )
            .performTextInput("password")


        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .performTextInput("passwod")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
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
    fun missingPasswordTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Email")
            )
            .performTextInput("test@email.com")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm New Password")
            )
            .performTextInput("passwod")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
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
    fun missingConfirmPasswordTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Email")
            )
            .performTextInput("test@email.com")

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter Password")
            )
            .performTextInput("password")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
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
    fun weakPasswordTest(){
        // activate the dialog directly
        signUpErrorMessage.value = "Weak Password"

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Weak Password")
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
                        hasText("Weak Password")
            )
            .assertDoesNotExist()
    }

    @Test
    fun malformedEmailTest(){
        // activate the dialog directly
        signUpErrorMessage.value = R.string.malformed_email_message

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Malformed email")
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
                        hasText("Malformed email")
            )
            .assertDoesNotExist()
    }

    @Test
    fun existingUserTest(){
        // activate the dialog directly
        signUpErrorMessage.value = R.string.user_collision_message

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("User with this email already exists! Please login or use a different email.")
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
                        hasText("User with this email already exists! Please login or use a different email.")
            )
            .assertDoesNotExist()
    }
}