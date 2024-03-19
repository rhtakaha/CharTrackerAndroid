package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.chartracker.database.CharacterEntity
import com.chartracker.ui.characters.AddEditCharacterScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddCharacterScreenTest{
    @get: Rule
    val composeTestRule = createComposeRule()
    private val uploadError =  mutableStateOf(false)
    private val duplicateNameError =  mutableStateOf(false)

    @Before
    fun setupAddScreen(){
        composeTestRule.setContent {
            val character = CharacterEntity()
            val charString = listOf("Aragorn", "Sauron", "Gimli")
            AddEditCharacterScreen(
                character = character,
                charactersStringList = charString,
                updateAllies = {_, _ ->},
                updateEnemies = {_, _ ->},
                updateNeutrals = {_, _ ->},
                submitCharacter = {_, _ ->},
                deleteCharacter = { /**/ },
                uploadError = uploadError.value,
                resetUploadError = {uploadError.value = false},
                retrievalError = false,
                resetRetrievalError = {},
                duplicateNameError = duplicateNameError.value,
                resetDuplicateNameError = {duplicateNameError.value = false},
                readyToNavToCharacters = false,
                navToCharacters = { /**/ },
                resetNavToCharacters = { /**/ },
                startImage = null,
                onBackNav = {}
            )
        }
    }

    @Test
    fun addCharacterContentsTest(){
        //Top bar
        composeTestRule
            .onNodeWithContentDescription("Submit character")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertHasClickAction()
            .assertIsDisplayed()

        //Character
        composeTestRule
            .onNodeWithText("Select image")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Name:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter name")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aliases:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter aliases")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Titles:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter titles")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Age:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter age")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Home:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter home")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gender:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter gender")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("AddEditCharacter Screen")
            .performTouchInput { swipeUp(endY = 1500.0F) }


        composeTestRule
            .onNodeWithText("Race:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter race (such as human)")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Living/Dead:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter living or dead")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Occupation:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter occupation")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("AddEditCharacter Screen")
            .performTouchInput { swipeUp() }

        composeTestRule
            .onNodeWithText("Weapon(s):")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter weapon(s)")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Tools/Equipment:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter tools/equipment")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bio:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter important info")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Faction:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter faction")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Allies:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Enemies:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Neutral:")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("Aragorn")
            .assertCountEquals(3)

        composeTestRule
            .onAllNodesWithText("Sauron")
            .assertCountEquals(3)

        composeTestRule
            .onAllNodesWithText("Gimli")
            .assertCountEquals(3)
    }

    @Test
    fun addCharacterEditFieldsTest(){
        composeTestRule
            .onNode(
                hasText("Enter name")
                        and
                        hasSetTextAction()
            )
            .performTextInput("Sauron")

        composeTestRule
            .onAllNodesWithText("Sauron")
            .assertCountEquals(4)
    }

    @Test
    fun uploadErrorTest(){
        // activate snackbar
        uploadError.value = true

        composeTestRule
            .onNodeWithText("Error uploading character!")
            .assertIsDisplayed()
    }
    @Test
    fun duplicateNameErrorTest(){
        // activate the dialog directly
        duplicateNameError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Duplicate name! Ensure each name in each story is unique.")
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
                        hasText("Duplicate name! Ensure each name in each story is unique.")
            )
            .assertDoesNotExist()
    }
}

class EditCharacterScreenTest{
    @get: Rule
    val composeTestRule = createComposeRule()
    private val uploadError =  mutableStateOf(false)
    private val retrievalError =  mutableStateOf(false)
    private val duplicateNameError =  mutableStateOf(false)

    @Before
    fun setupEditScreen(){
        composeTestRule.setContent {
            val character = CharacterEntity(
                name = "Aragorn",
                aliases = "Strider",
                occupation = "Ranger then King",
                titles = " of Gondor and Arnor",
                gender = "Male"
            )
            val charString = listOf("Aragorn", "Sauron", "Gimli")
            AddEditCharacterScreen(
                character = character,
                editing = true,
                charactersStringList = charString,
                updateAllies = {_, _ ->},
                updateEnemies = {_, _ ->},
                updateNeutrals = {_, _ ->},
                submitCharacter = {_, _ ->},
                deleteCharacter = { /**/ },
                uploadError = uploadError.value,
                resetUploadError = {uploadError.value = false},
                retrievalError = retrievalError.value,
                resetRetrievalError = {retrievalError.value = false},
                duplicateNameError = duplicateNameError.value,
                resetDuplicateNameError = {duplicateNameError.value = false},
                readyToNavToCharacters = false,
                navToCharacters = { /**/ },
                resetNavToCharacters = { /**/ },
                startImage = null,
                onBackNav = {}
            )
        }
    }

    @Test
    fun editCharacterContentsTest(){
        //Top bar
        composeTestRule
            .onNodeWithContentDescription("Submit character")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Delete")
            .assertHasClickAction()
            .assertIsDisplayed()

        //Character
        composeTestRule
            .onNodeWithText("Select image")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Name:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Aragorn")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Aliases:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Strider")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Titles:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText(" of Gondor and Arnor")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Age:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter age")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Home:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter home")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Gender:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Male")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("AddEditCharacter Screen")
            .performTouchInput { swipeUp(endY = 1500.0F) }


        composeTestRule
            .onNodeWithText("Race:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter race (such as human)")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Living/Dead:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter living or dead")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Occupation:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Ranger then King")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("AddEditCharacter Screen")
            .performTouchInput { swipeUp() }

        composeTestRule
            .onNodeWithText("Weapon(s):")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter weapon(s)")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Tools/Equipment:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter tools/equipment")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bio:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter important info")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Faction:")
            .assertIsDisplayed()
        composeTestRule
            .onNode(
                hasText("Enter faction")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Allies:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Enemies:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Neutral:")
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("Aragorn")
            .assertCountEquals(4)

        composeTestRule
            .onAllNodesWithText("Sauron")
            .assertCountEquals(3)

        composeTestRule
            .onAllNodesWithText("Gimli")
            .assertCountEquals(3)
    }

    @Test
    fun editCharacterEditFieldsTest(){
        composeTestRule
        composeTestRule
            .onNode(
                hasText(" of Gondor and Arnor")
                        and
                        hasSetTextAction()
            )
            .performTextInput("King")

        composeTestRule
            .onNodeWithText("King of Gondor and Arnor")
            .assertIsDisplayed()
    }

    @Test
    fun uploadErrorTest(){
        // activate snackbar
        uploadError.value = true

        composeTestRule
            .onNodeWithText("Error uploading character!")
            .assertIsDisplayed()
    }

    @Test
    fun retrievalErrorTest(){
        // activate snackbar
        retrievalError.value = true

        composeTestRule
            .onNodeWithText("Error retrieving character data!")
            .assertIsDisplayed()
    }

    @Test
    fun duplicateNameErrorTest(){
        // activate the dialog directly
        duplicateNameError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Duplicate name! Ensure each name in each story is unique.")
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
                        hasText("Duplicate name! Ensure each name in each story is unique.")
            )
            .assertDoesNotExist()
    }
}