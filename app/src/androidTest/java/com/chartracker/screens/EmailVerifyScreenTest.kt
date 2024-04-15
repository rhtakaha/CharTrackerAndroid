package com.chartracker.screens


import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.chartracker.ui.auth.EmailVerifyScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EmailVerifyScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val failedReload = mutableStateOf(false)
    private val emailSent = mutableStateOf(false)

    @Before
    fun setupEmailVerifyScreen(){
        composeTestRule.setContent {
            EmailVerifyScreen(
                email = "test@email.com",
                sendEmail = { /**/ },
                changeEmail = { /**/ },
                checkVerification = { -> false },
                failedReload = failedReload.value,
                resetFailedReload = {failedReload.value = false},
                emailSent = emailSent.value,
                resetEmailSent = {emailSent.value = false},
                navToStories = { /**/ })
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

    @Test
    fun failedReloadTest(){
        // activate the dialog
        failedReload.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Error with account!")
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
                        hasText("Error with account!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun emailSentTest(){
        // activate snackbar
        emailSent.value = true

        composeTestRule
            .onNodeWithText("Email sent!")
            .assertIsDisplayed()
    }
}