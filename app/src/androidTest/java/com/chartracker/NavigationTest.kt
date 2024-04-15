package com.chartracker

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.chartracker.database.MockCharacterDB
import com.chartracker.database.MockImageDB
import com.chartracker.database.MockStoryDB
import com.chartracker.database.MockUserDB
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get: Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupCharTrackerNavHost(){
        composeTestRule.setContent {
            // create TestNavHostController
            navController = TestNavHostController(LocalContext.current)

            // Sets a ComposeNavigator to the navController so it can navigate through composables
            navController.navigatorProvider.addNavigator(
                ComposeNavigator()
            )
            CharTrackerNavHost(
                navController = navController,
                userDB = MockUserDB(),
                storyDB = MockStoryDB(),
                imageDB = MockImageDB(),
                characterDB = MockCharacterDB()
            )
        }
    }

    /** Sign In **/
    @Test
    fun charTrackerNavHost_initialDestinationTest(){
        composeTestRule
            .onNodeWithContentDescription("SignIn Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_clickSignUpButton_signInNavigatesToSignUpTest(){
        composeTestRule
            .onNodeWithText("Sign Up")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("SignUp Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_clickSignUpButton_signInNavigatesToSignUp_routeTest(){
        composeTestRule
            .onNodeWithText("Sign Up")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "signUp")
    }

    @Test
    fun charTrackerNavHost_SignInNavigatesToStoriesTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }

    /** Sign Up **/
    @Test
    fun charTrackerNavHost_clickSignUpButton_signUpNavigatesToEmailVerifyTest(){
        // goto sign up screen
        composeTestRule
            .onNodeWithText("Sign Up")
            .performClick()

        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("pass123")

        composeTestRule
            .onNodeWithText("Confirm New Password")
            .performTextInput("pass123")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("EmailVerify Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_signUp_upTest(){
        // goto sign up screen
        composeTestRule
            .onNodeWithText("Sign Up")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("SignIn Screen")
            .assertIsDisplayed()
    }

    /** Email Verify **/
    @Test
    fun charTrackerNavHost_EmailVerifyNavigatesToStoriesTest(){
        // go to sign up screen
        composeTestRule
            .onNodeWithText("Sign Up")
            .performClick()

        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("pass123")

        composeTestRule
            .onNodeWithText("Confirm New Password")
            .performTextInput("pass123")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Up")
            )
            .performClick()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("I'm verified!")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }

    /** Stories **/
    @Test
    fun charTrackerNavHost_StoriesNavigatesToAddStoriesTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a new story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("AddEditStory Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_StoriesNavigatesToSettingsTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_StoriesNavigatesToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }

    /** Characters **/
    @Test
    fun charTrackerNavHost_Characters_upTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_CharactersNavigatesToSettingsTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_CharactersNavigatesToEditStoryTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("AddEditStory Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_CharactersNavigatesToAddCharacterTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("AddEditCharacter Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_CharactersNavigatesToCharacterDetailsTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithText("Frodo")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("CharacterDetails Screen")
            .assertIsDisplayed()
    }

    /** Character Details **/
    @Test
    fun charTrackerNavHost_CharacterDetailsNavigatesToEditCharacterTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithText("Frodo")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit Character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("AddEditCharacter Screen")
            .assertIsDisplayed()
    }
    @Test
    fun charTrackerNavHost_CharacterDetailsUpToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithText("Frodo")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }

    /** Edit Character **/
    @Test
    fun charTrackerNavHost_EditCharacterUpToCharacterDetailsTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithText("Frodo")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit Character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("CharacterDetails Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_EditCharacterNavigatesToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithText("Frodo")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit Character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Submit character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_EditCharacterNavigatesToCharacters_upBackstackCheckTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithText("Frodo")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit Character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Submit character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule.onRoot().printToLog("after up:")

        composeTestRule
            .onNodeWithContentDescription("CharacterDetails Screen")
            .assertIsDisplayed()
    }

    /** Add Character **/
    @Test
    fun charTrackerNavHost_AddCharacterUpToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_AddCharacterNavigatesToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a character")
            .performClick()

        composeTestRule
            .onNodeWithText("Enter name")
            .performTextInput("true")

        composeTestRule
            .onNodeWithContentDescription("Submit character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_AddCharacterNavigatesToCharacters_upBackstackCheckTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a character")
            .performClick()

        composeTestRule
            .onNodeWithText("Enter name")
            .performTextInput("true")

        composeTestRule
            .onNodeWithContentDescription("Submit character")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }


    /** Settings **/

    @Test
    fun charTrackerNavHost_SettingsNavigateToSignIn_signOutTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign Out")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("SignIn Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_SettingsNavigateToSignIn_deleteAccountTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("DELETE ACCOUNT")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("SignIn Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_SettingsUpToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }
    @Test
    fun charTrackerNavHost_SettingsUpToStoriesTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Settings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }

    /** Edit Story **/
    @Test
    fun charTrackerNavHost_EditStoryUpToCharactersTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Characters Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_EditStoryNavigatesToStoriesTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")

        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithText("Lord of the Rings")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Edit story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Submit story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }

    /** Add Story **/
    @Test
    fun charTrackerNavHost_AddStoriesNavigatesToStoriesTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a new story")
            .performClick()

        composeTestRule
            .onNodeWithText("Enter title")
            .performTextInput("title")

        composeTestRule
            .onNodeWithContentDescription("Submit story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }

    @Test
    fun charTrackerNavHost_AddStories_upTest(){
        composeTestRule
            .onNodeWithText("Enter Email")
            .performTextInput("email@email.com")

        composeTestRule
            .onNodeWithText("Enter Password")
            .performTextInput("correct")


        composeTestRule
            .onNode(
                hasClickAction()
                        and
                        hasText("Sign In")
            )
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add a new story")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Up button")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Stories Screen")
            .assertIsDisplayed()
    }
}