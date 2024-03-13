package com.chartracker.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
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
}

class EditStoryScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()
    private val uploadError =  mutableStateOf(false)

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
                submitStory = {_, _ ->},
                deleteStory = { /**/ },
                uploadError = uploadError.value,
                resetUploadError = {uploadError.value = false},
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
}