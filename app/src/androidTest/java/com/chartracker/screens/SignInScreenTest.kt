package com.chartracker.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.auth.SignInScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignInScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupSignInScreen(){
        composeTestRule.setContent {
            SignInScreen(navToSignUp = { /*TODO*/ })
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
}