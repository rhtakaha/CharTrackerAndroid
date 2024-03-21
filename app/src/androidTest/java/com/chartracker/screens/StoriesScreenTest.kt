package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.chartracker.database.StoryEntity
import com.chartracker.ui.story.StoriesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StoriesScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val failedGetStories =  mutableStateOf(false)

    @Before
    fun setupStoriesScreen(){
        composeTestRule.setContent {
            val stories = listOf(
                StoryEntity(
                    name = "Lord of the Rings",
                    imagePublicUrl = "https://wallpapercave.com/wp/wp2770636.png",
                    imageFilename = "LotR"),
                StoryEntity(name = "Ender's Game"),
                StoryEntity(name = "Batman"),
                StoryEntity(name = "Game of Thrones"),
                StoryEntity(name = "The Chronicles of Narnia"),
                StoryEntity(name = "Really Really Really Really Long Ahh Title Just to see how it looks"),
                StoryEntity(name = "The Cthulhu Mythos"),

            )
            StoriesScreen(
                stories = stories,
                refreshStories = {},
                alphaSort = {},
                reverseAlphaSort = {},
                recentSort = {},
                reverseRecentSort = {},
                failedGetStories = failedGetStories.value,
                resetFailedGetStories = {failedGetStories.value = false},
                navToAddStory = { /**/ },
                navToCharacters = {},
                navToSettings = { /**/ }) {

            }
        }
    }

    @Test
    fun storiesContentTest(){
        //Top bar
        composeTestRule
            .onNodeWithText("Stories")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()
            .assertHasClickAction()

        //screen content
        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Entity image")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ender's Game")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Batman")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Game of Thrones")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("The Chronicles of Narnia")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .performTouchInput { swipeUp() }

        composeTestRule
            .onNodeWithText("The Cthulhu Mythos")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Really Really Really Really Long Ahh Title Just to see how it looks")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Add a new story")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun failedGetStoriesTest(){
        // activate snackbar
        failedGetStories.value = true

        composeTestRule
            .onNodeWithText("Issue getting stories. Refresh to try again.")
            .assertIsDisplayed()
    }
}