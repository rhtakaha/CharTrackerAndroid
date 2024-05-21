package com.chartracker.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import org.junit.Rule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.chartracker.R
import com.chartracker.ui.components.TextAndContentHolder
import com.chartracker.ui.components.TextEntryHolder
import org.junit.Test

class TextElementsTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTextAndContentHolder(){
        composeTestRule.setContent {
            TextAndContentHolder(title = R.string.name, body = "Frodo Baggins")
        }

        composeTestRule
            .onNodeWithText("Name:")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Frodo Baggins")
            .assertIsDisplayed()
    }

    @Test
    fun testTextEntryHolderTitleTest(){
        composeTestRule.setContent {
            var password by remember { mutableStateOf("") }
            TextEntryHolder(
                title = R.string.password,
                label = R.string.passwordHint,
                text = password,
                onTyping = {newInput -> password = newInput})
        }

        composeTestRule
            .onNodeWithText("Password:")
            .assertIsDisplayed()
    }

    @Test
    fun testTextEntryHolderPasswordObscuredTest(){
        composeTestRule.setContent {
            var password by remember { mutableStateOf("") }
            TextEntryHolder(
                title = R.string.password,
                label = R.string.passwordHint,
                text = password,
                onTyping = {newInput -> password = newInput},
                isPassword = true)
        }

        composeTestRule
            .onNodeWithText("Enter password")
            .performTextInput("pass123")

        composeTestRule
            .onNodeWithText("•••••••")
            .assertIsDisplayed()
    }

    @Test
    fun testTextEntryHolderPasswordHideAndRevealTest(){
        composeTestRule.setContent {
            var password by remember { mutableStateOf("") }
            TextEntryHolder(
                title = R.string.password,
                label = R.string.passwordHint,
                text = password,
                onTyping = {newInput -> password = newInput},
                isPassword = true)
        }

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("pass123")

        composeTestRule
            .onNodeWithText("•••••••")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasContentDescription("Show password")
                and
                hasClickAction()
            )
            .performClick()

        composeTestRule
            .onNodeWithText("•••••••")
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithText("pass123")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasContentDescription("Hide password")
                        and
                        hasClickAction()
            )
            .performClick()

        composeTestRule
            .onNodeWithText("pass123")
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithText("•••••••")
            .assertIsDisplayed()
    }

    @Test
    fun testTextEntryHolderRegularNotObscuredTest(){
        composeTestRule.setContent {
            var password by remember { mutableStateOf("") }
            TextEntryHolder(
                title = R.string.password,
                label = R.string.passwordHint,
                text = password,
                onTyping = {newInput -> password = newInput})
        }

        composeTestRule
            .onNodeWithText("Enter password")
            .performTextInput("some text")

        composeTestRule
            .onNodeWithText("some text")
            .assertIsDisplayed()
    }

    @Test
    fun testTextEntryHolderEmailNotObscuredTest(){
        composeTestRule.setContent {
            var email by remember { mutableStateOf("") }
            TextEntryHolder(
                title = R.string.email,
                label = R.string.emailHint,
                text = email,
                onTyping = {newInput -> email = newInput},
                isEmail = true)
        }

        composeTestRule
            .onNodeWithText("Enter email")
            .performTextInput("some text")

        composeTestRule
            .onNodeWithText("some text")
            .assertIsDisplayed()
    }
}