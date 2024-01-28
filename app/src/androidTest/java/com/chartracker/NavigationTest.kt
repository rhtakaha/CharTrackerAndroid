package com.chartracker

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
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
            CharTrackerNavHost(navController = navController)
        }
    }

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

    /* MESSY because CharTrackerNavHost calls the ViewModel version of the screens
        so it takes real actions.
        Could setup emulator to do it BUT
        also have the issue of having to wait for operations to be performed
        Could make a Nav Test version that does not use the ViewModel versions*/
//    @Test
//    fun charTrackerNavHost_clickSignUpButton_signUpNavigatesToEmailVerifyTest(){
//        // goto sign up screen
//        composeTestRule
//            .onNodeWithText("Sign Up")
//            .performClick()
//
//        composeTestRule
//            .onNodeWithText("Enter email")
//            .performTextInput("test@email.com")
//
//        composeTestRule
//            .onNodeWithText("Enter password")
//            .performTextInput("pass123")
//
//        composeTestRule
//            .onNodeWithText("Confirm password")
//            .performTextInput("pass123")
//
//        composeTestRule.onRoot().printToLog("After Inputs:")
//
//        composeTestRule
//            .onNode(
//                hasClickAction()
//                        and
//                        hasText("Sign Up")
//            )
//            .performClick()
//
//        composeTestRule.onRoot().printToLog("After submit:")
//
//        composeTestRule
//            .onNodeWithContentDescription("EmailVerify Screen")
//            .assertIsDisplayed()
//    }
}