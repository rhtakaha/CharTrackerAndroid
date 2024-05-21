package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.chartracker.database.StoryEntity
import com.chartracker.ui.story.AddEditStoryScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddStoryScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val uploadError =  mutableStateOf(false)
    private val retrievalError =  mutableStateOf(false)
    private val duplicateTitleError =  mutableStateOf(false)

    @Before
    fun setupAddScreen(){
        composeTestRule.setContent {
            val story = StoryEntity()
            AddEditStoryScreen(
                story = story,
                submitStory = {_, _ ->},
                deleteStory = { /**/ },
                uploadError = uploadError.value,
                resetUploadError = {uploadError.value = false},
                retrievalError = retrievalError.value,
                resetRetrievalError = {retrievalError.value = false},
                duplicateTitleError = duplicateTitleError.value,
                resetDuplicateTitleError = { duplicateTitleError.value = false},
                readyToNavToStories = false,
                navToStories = { /**/ },
                resetNavToStories = { /**/ },
                startImage = null,
                onBackNav = {}
            )
        }
    }

    @Test
    fun addStoryContentsTest(){
        composeTestRule
            .onNodeWithContentDescription("Submit story")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Select image")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Title:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter title")
                and
                hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Author:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter author")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Genre:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter genre")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Type:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter type (e.g. book)")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()
    }

    @Test
    fun addStoryEditFieldsTest(){
        composeTestRule
            .onNode(
                hasText("Enter title")
                        and
                        hasSetTextAction()
            )
            .performTextInput("Lord of the Rings")

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter author")
                        and
                        hasSetTextAction()
            )
            .performTextInput("JRR Tolkien")

        composeTestRule
            .onNodeWithText("JRR Tolkien")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter genre")
                        and
                        hasSetTextAction()
            )
            .performTextInput("Epic Fantasy")

        composeTestRule
            .onNodeWithText("Epic Fantasy")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter type (e.g. book)")
                        and
                        hasSetTextAction()
            )
            .performTextInput("Book/Movie")

        composeTestRule
            .onNodeWithText("Book/Movie")
            .assertIsDisplayed()
    }

    @Test
    fun confirmUpTest(){
        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Are you sure you want to go back? All current progress will be lost?")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Cancel")
                        and
                        hasClickAction()
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Are you sure you want to go back? All current progress will be lost?")
            .assertIsNotDisplayed()
    }

    @Test
    fun uploadErrorTest(){
        // activate the dialog directly
        uploadError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Error uploading story!")
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
                        hasText("Error uploading story!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun duplicateTitleErrorTest(){
        // activate the dialog directly
        duplicateTitleError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Duplicate title! Ensure each title is unique.")
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
                        hasText("Duplicate title! Ensure each title is unique.")
            )
            .assertDoesNotExist()
    }
}

class EditStoryScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val uploadError =  mutableStateOf(false)
    private val retrievalError =  mutableStateOf(false)
    private val duplicateTitleError =  mutableStateOf(false)

    @Before
    fun setupEditScreen(){
        composeTestRule.setContent {
            val story = StoryEntity(
                name = "Ender's Game",
                author = "Scott Card",
                genre = "Sci-Fi"
            )
            AddEditStoryScreen(
                story = story,
                editing = true,
                submitStory = {_, _ ->},
                deleteStory = { /**/ },
                uploadError = uploadError.value,
                resetUploadError = {uploadError.value = false},
                retrievalError = retrievalError.value,
                resetRetrievalError = {retrievalError.value = false},
                duplicateTitleError = duplicateTitleError.value,
                resetDuplicateTitleError = { duplicateTitleError.value = false},
                readyToNavToStories = false,
                navToStories = { /**/ },
                resetNavToStories = { /**/ },
                startImage = null,
                onBackNav = {}
            )
        }
    }

    @Test
    fun editStoryContentsTest(){
        composeTestRule
            .onNodeWithContentDescription("Submit story")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Select image")
            .assertHasClickAction()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Title:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ender's Game")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Author:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Scott Card")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Genre:")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sci-Fi")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Type:")
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("Enter type (e.g. book)")
                        and
                        hasSetTextAction()
            )
            .assertIsDisplayed()
    }

    @Test
    fun editStoryEditFieldsTest(){
        composeTestRule
            .onNodeWithText("Scott Card")
            .performTextInput("Orson ")


        composeTestRule.onRoot().printToLog("current")


        composeTestRule
            .onNodeWithText("Orson Scott Card")
            .assertIsDisplayed()
    }

    @Test
    fun confirmDeleteTest(){
        composeTestRule
            .onNode(
                hasContentDescription("Delete")
                and
                hasClickAction()
            )
            .performClick()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Are you sure you want to delete this story? It is irreversible!")
            )
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Cancel")
                        and
                        hasClickAction()
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Are you sure you want to delete this story? It is irreversible!")
            .assertIsNotDisplayed()
    }

    @Test
    fun uploadErrorTest(){
        // activate the dialog directly
        uploadError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Error uploading story!")
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
                        hasText("Error uploading story!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun retrievalErrorTest(){
        // activate the dialog directly
        retrievalError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Error retrieving the data!")
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
                        hasText("Error retrieving the data!")
            )
            .assertDoesNotExist()
    }

    @Test
    fun duplicateTitleErrorTest(){
        // activate the dialog directly
        duplicateTitleError.value = true

        composeTestRule
            .onNode(
                hasAnyAncestor(isDialog())
                        and
                        hasText("Duplicate title! Ensure each title is unique.")
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
                        hasText("Duplicate title! Ensure each title is unique.")
            )
            .assertDoesNotExist()
    }
}