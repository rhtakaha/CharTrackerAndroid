package com.chartracker.screens

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.chartracker.database.CharacterEntity
import com.chartracker.database.StoryEntity
import com.chartracker.ui.characters.CharactersScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharactersScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupCharactersScreen(){
        composeTestRule.setContent {
            val characters = listOf(
                CharacterEntity(name = "Frodo Baggins"),
                CharacterEntity(name = "Sam"),
                CharacterEntity(name = "Merry"),
                CharacterEntity(name = "Pippin"),
                CharacterEntity(name = "Gandalf"),
                CharacterEntity(name = "Borimir"),
                CharacterEntity(name = "Aragorn"),
                CharacterEntity(name = "Gimli"),
                CharacterEntity(name = "Legolas"),

                )
            val story = StoryEntity(name = "Lord of the Rings",
                author = "JRR Tolkien",
                genre = "Epic Fantasy",
                type = "Book/film",
                imagePublicUrl = "https://wallpapercave.com/wp/wp2770636.png",
                imageFilename = "LotR")
            CharactersScreen(
                characters = characters,
                refreshCharacters = {},
                story = story,
                navToAddCharacter = { /**/ },
                navToCharacterDetails = {},
                navToEditStory = { /**/ },
                navToSettings = { /**/ }) {
            }
        }
    }
    @Test
    fun charactersContentTest(){
        //Top bar
        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Edit story")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()
            .assertHasClickAction()

        //Story Details
        composeTestRule
            .onNodeWithText("JRR Tolkien")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Epic Fantasy")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Book/film")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Image representing the story")
            .assertIsDisplayed()

        //Characters
        composeTestRule
            .onNodeWithText("Frodo Baggins")
            .assertHasClickAction()
            .assertIsDisplayed()


        composeTestRule
            .onNodeWithText("Sam")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Merry")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pippin")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gandalf")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .performTouchInput { swipeUp() }

        composeTestRule
            .onNodeWithText("Borimir")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aragorn")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gimli")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Legolas")
            .assertHasClickAction()
            .assertIsDisplayed()
    }
}