package com.chartracker.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.chartracker.database.CharacterEntity
import com.chartracker.database.StoryEntity
import com.chartracker.ui.components.EntityHolder
import com.chartracker.ui.components.EntityHolderList
import com.chartracker.ui.components.StoryDetails
import org.junit.Rule
import org.junit.Test

class EntityElementsTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    // Entity Holder tests
    @Test
    fun entityHolderNoImageTest(){
        composeTestRule.setContent {
            EntityHolder(
                imageUrl = null,
                entityName = "Aragorn",
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Aragorn")
            .assertIsDisplayed()
    }

    @Test
    fun entityHolderWithImageTest(){
        composeTestRule.setContent {
            EntityHolder(
                imageUrl = "https://upload.wikimedia.org/wikipedia/en/3/35/Aragorn300ppx.png",
                entityName = "Aragorn",
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Aragorn")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Entity image")
            .assertIsDisplayed()
    }

    // Entity Holder list
    @Test
    fun entityHolderListCharactersTest(){
        val characters = listOf(
            CharacterEntity(name = "Gandalf"),
            CharacterEntity(name = "Gollum"),
            CharacterEntity(name = "Sam"),
            CharacterEntity(name = "Meriadoc Brandybuck"),
            CharacterEntity(name = "Peregrin Took"),
        )

        composeTestRule.setContent {
            EntityHolderList(entities = characters, onClick = {})
        }

        composeTestRule
            .onNodeWithText("Gandalf")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gollum")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sam")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Meriadoc Brandybuck")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Peregrin Took")
            .assertIsDisplayed()
    }

    @Test
    fun entityHolderListCharactersWithStoryTest(){
        val characters = listOf(
            CharacterEntity(name = "Gandalf"),
            CharacterEntity(name = "Gollum"),
            CharacterEntity(name = "Sam"),
            CharacterEntity(name = "Meriadoc Brandybuck"),
            CharacterEntity(name = "Peregrin Took"),
        )
        val story = StoryEntity(
            name = "Lord of the Rings",
            genre = "Epic Fantasy",
            author = "JRR Tolkien",
            type = "Book/Movie",
            imagePublicUrl = "https://wallpapercave.com/wp/wp2770636.png",
            imageFilename = "lotr"
        )

        composeTestRule.setContent {
            EntityHolderList(
                entities = characters,
                onClick = {},
                story = story
            )
        }

        composeTestRule
            .onNodeWithText("Epic Fantasy")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JRR Tolkien")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Book/Movie")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Image representing the story")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gandalf")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gollum")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sam")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Meriadoc Brandybuck")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Peregrin Took")
            .assertIsDisplayed()
    }

    @Test
    fun entityHolderListStoriesTest(){
        val stories = listOf(
            StoryEntity(name = "Lord of the Rings"),
            StoryEntity(name = "Indiana Jones"),
            StoryEntity(name = "Star Wars"),
            StoryEntity(name = "Overwatch"),
            StoryEntity(name = "Dune"),
        )

        composeTestRule.setContent {
            EntityHolderList(entities = stories, onClick = {})
        }

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Indiana Jones")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Star Wars")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Overwatch")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Dune")
            .assertIsDisplayed()

    }

    // Story Details
    @Test
    fun storyDetailsWithImageTest(){
        val story = StoryEntity(
            name = "Lord of the Rings",
            genre = "Epic Fantasy",
            author = "JRR Tolkien",
            imagePublicUrl = "https://wallpapercave.com/wp/wp2770636.png",
            imageFilename = "lotr"
        )
        composeTestRule.setContent {
            StoryDetails(story = story)
        }

        composeTestRule
            .onNodeWithText("Epic Fantasy")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JRR Tolkien")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Image representing the story")
            .assertIsDisplayed()

    }

    @Test
    fun storyDetailsTest(){
        val story = StoryEntity(
            name = "Lord of the Rings",
            author = "JRR Tolkien",
            type = "Book/Movie",
            imagePublicUrl = null,
            imageFilename = null
        )
        composeTestRule.setContent {
            StoryDetails(story = story)
        }

        composeTestRule
            .onNodeWithText("Book/Movie")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JRR Tolkien")
            .assertIsDisplayed()

    }
}