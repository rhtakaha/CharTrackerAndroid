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
import com.chartracker.ui.auth.EmailVerifyScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class EmailVerifyScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val failedReload = mutableStateOf(false)
    private val emailSent = mutableStateOf(false)
    private val invalidUser =  mutableStateOf(false)
    private val updateEmailVerificationSent =  mutableStateOf(false)

    @Before
    fun setupEmailVerifyScreen(){
        val updateEmail = mutableStateOf("")
        composeTestRule.setContent {
            EmailVerifyScreen(
                email = "test@email.com",
                updatedEmail = updateEmail.value,
                onUpdatedEmailChange = {new:String -> updateEmail.value = new},
                submitUpdatedEmail = {},
                updateEmailVerificationSent = updateEmailVerificationSent.value,
                resetUpdateEmailVerificationSent = {updateEmailVerificationSent.value = false},
                invalidUser = invalidUser.value,
                resetInvalidUser = {invalidUser.value = false},
                sendEmail = { /**/ },
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
            .onNodeWithText("If the email is incorrect:")
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
    fun emailInputTest(){
        composeTestRule
            .onNode(
                hasSetTextAction()
                        and
                        hasText("Enter New Email")
            )
            .performTextInput("test2@email.com")

        composeTestRule
            .onNodeWithText("test2@email.com")
            .assertIsDisplayed()
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
                        hasText("Change email")
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