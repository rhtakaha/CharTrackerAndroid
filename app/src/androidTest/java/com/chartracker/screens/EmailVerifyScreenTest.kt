package com.chartracker.screens


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.chartracker.ui.auth.EmailVerifyScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EmailVerifyScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupEmailVerifyScreen(){
        composeTestRule.setContent {
            EmailVerifyScreen(
                email = "test@email.com",
                sendEmail = { /*TODO*/ },
                changeEmail = { /*TODO*/ },
                checkVerification = { -> false },
                failedReload = false,
                resetFailedReload = {},
                navToStories = { /*TODO*/ }) {

            }
        }
    }

    @Test
    fun screenContentsTest(){
        composeTestRule
            .onNodeWithContentDescription("Top bar")
            .assertIsDisplayed()


        composeTestRule
            .onNodeWithText("A verification email was sent to the below email. Please verify to continue.")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test@email.com")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Resend")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Change email")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("I'm verified!")
            )
            .assertIsDisplayed()
    }
}