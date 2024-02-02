package com.chartracker.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.chartracker.ui.components.CharTrackerTopBar
import org.junit.Rule
import org.junit.Test
import com.chartracker.R
class AppBarTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun defaultTopBarTest(){
        composeTestRule.setContent {
            CharTrackerTopBar(onBackNav = { /*TODO*/ },actionButtons = {})
        }

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()
    }

    @Test
    fun topBarWithTitleTest(){
        composeTestRule.setContent {
            CharTrackerTopBar(
                title = R.string.stories,
                onBackNav = { /*TODO*/ }, actionButtons = {})
        }

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Stories")
            .assertIsDisplayed()
    }

    @Test
    fun topBarWithOneActionButtonTest(){
        composeTestRule.setContent {
            CharTrackerTopBar(onBackNav = { /*TODO*/ }, actionButtons = {
                IconButton(onClick = { /* do something */ },
                    modifier = Modifier.semantics { contentDescription = "menu button" }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description"
                    )
                }
            })
        }

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("menu button")
            .assertIsDisplayed()
    }

    @Test
    fun topBarWithTwoActionButtonsTest(){
        composeTestRule.setContent {
            CharTrackerTopBar(onBackNav = { /*TODO*/ }, actionButtons = {
                IconButton(onClick = { /* do something */ },
                    modifier = Modifier.semantics { contentDescription = "menu button" }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { /* do something */ },
                    modifier = Modifier.semantics { contentDescription = "spa button" }) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = "Localized description"
                    )
                }
            })
        }

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("menu button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("spa button")
            .assertIsDisplayed()
    }
}