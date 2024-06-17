package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.chartracker.database.CharacterEntity
import com.chartracker.ui.characters.CharacterDetailsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterDetailsScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val failedGetCharacter =  mutableStateOf(false)

    @Before
    fun setupCharactersScreen(){
        val character = CharacterEntity(
            name = "Sauron",
            aliases = "The Necromancer",
            titles = "Dark Lord",
            home = "Mordor",
            race = "Maiar",
            weapons = "rings of power",
            faction = listOf("Mordor"),
            bio = "Former Lieutenant of the first Dark Lord (Morgoth). After Morgoth's fall in the First Age, Sauron feigned repentance and then hid in Middle Earth to continue his master's plans",
            imagePublicUrl = "https://th.bing.com/th/id/OIP.hDXdPpm97mwk0wIDSDtQwQAAAA?rs=1&pid=ImgDetMain",
            imageFilename = "SauronImage"
        )
        composeTestRule.setContent {
            CharacterDetailsScreen(
                character = character,
                failedGetCharacter = failedGetCharacter.value,
                resetFailedGetCharacter = {failedGetCharacter.value = false},
                refresh = {},
                alliesList = null,
                enemiesList = null,
                neutralList = null,
                factionList = "Mordor",
                navToEditCharacter = { /**/ }) {
            }
        }
    }

    @Test
    fun characterDetailsContentsTest(){
        //Top bar
        composeTestRule
            .onAllNodesWithText("Sauron")
            .assertCountEquals(2)

        composeTestRule
            .onNodeWithContentDescription("Edit Character")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertIsDisplayed()
            .assertHasClickAction()

        // Character Details
        composeTestRule
            .onNodeWithContentDescription("Image representing the character")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Name:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aliases:")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("The Necromancer")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Titles:")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Dark Lord")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Home:")
            .assertIsDisplayed()
        composeTestRule
            .onAllNodesWithText("Mordor")
            .assertCountEquals(2)

        composeTestRule
            .onNodeWithText("Race:")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Maiar")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Weapon(s):")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("rings of power")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Faction:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bio:")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Former Lieutenant of the first Dark Lord (Morgoth). After Morgoth's fall in the First Age, Sauron feigned repentance and then hid in Middle Earth to continue his master's plans")
            .assertIsDisplayed()
    }

    @Test
    fun failedGetCharacterTest(){
        // activate snackbar
        failedGetCharacter.value = true

        composeTestRule
            .onNodeWithText("Issue getting the character. Return to try again.")
            .assertIsDisplayed()
    }
}