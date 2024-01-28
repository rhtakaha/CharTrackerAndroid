package com.chartracker.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.chartracker.ui.auth.SignUpScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SignUpScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupSignUpScreen(){
        composeTestRule.setContent {
            SignUpScreen(navToEmailVerify = {}, onBackNav = { /*TODO*/ })
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
            .onNodeWithText("Confirm password:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm password")
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
    fun confirmPasswordObscuredTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Confirm password")
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