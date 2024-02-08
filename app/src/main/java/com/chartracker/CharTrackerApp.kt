package com.chartracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chartracker.ui.auth.SignInScreen
import com.chartracker.ui.auth.SignUpScreen
import com.chartracker.ui.theme.CharTrackerTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chartracker.ui.auth.EmailVerifyScreen
import com.chartracker.ui.characters.AddEditCharacterScreen
import com.chartracker.ui.characters.CharacterDetailsScreen
import com.chartracker.ui.characters.CharactersScreen
import com.chartracker.ui.story.AddEditStoryScreen
import com.chartracker.ui.story.StoriesScreen

@Composable
fun CharTrackerApp(){
    CharTrackerTheme {
        val navController = rememberNavController()

        //peek at top of backstack to know where we are then compare with destinations to know which it is
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = charTrackerScreens.find { it.route == currentDestination?.route } ?: SignIn
        CharTrackerNavHost(navController = navController)
    }
}

@Composable
fun CharTrackerNavHost(
    navController: NavHostController
){
    NavHost(navController = navController, startDestination = SignIn.route) {
        composable(route = SignIn.route){
            SignInScreen(
                navToSignUp = { navController.navigateSingleTopTo(SignUp.route) },
                navToStories = {navController.navigateSingleTopTo(Stories.route)})
        }
        composable(route = SignUp.route){
            SignUpScreen(
                navToEmailVerify = {email -> navController.navigateSingleTopTo("${EmailVerify.route}/$email") },
                onBackNav = {navController.navigateUp()}
            )
        }
        composable(
            route= EmailVerify.routeWithArgs,
            arguments = EmailVerify.arguments
        ){
            navBackStackEntry ->
            val userEmail = navBackStackEntry.arguments?.getString(EmailVerify.userEmailArg) ?: ""
            EmailVerifyScreen(
                navToUpdateEmail = { /*TODO*/ },
                navToStories = {navController.navigateSingleTopTo(Stories.route)},
                userEmail = userEmail,
                onBackNav = {navController.navigateUp()})
        }
        composable(route = Stories.route){
            StoriesScreen(
                navToAddStory = { navController.navigateSingleTopTo(AddEditStory.route) },
                navToCharacters = { storyTitle -> navController.navigateSingleTopTo("${Characters.route}/$storyTitle") },
                onBackNav = {navController.navigateUp()})
        }
        composable(
            route= AddEditStory.routeWithArgs,
            arguments = AddEditStory.arguments
        ){
            navBackStackEntry ->
            val storyId = navBackStackEntry.arguments?.getString(AddEditStory.storyIdArg)
            AddEditStoryScreen(
                onBackNav = {navController.navigateSingleTopTo(Stories.route)},
                storyId = storyId
                )
        }
        composable(
            route= Characters.routeWithArgs,
            arguments = Characters.arguments
        ){
            navBackStackEntry ->
            val storyTitle = navBackStackEntry.arguments?.getString(Characters.storyTitleArg)
            if (storyTitle != null) {
                CharactersScreen(
                    navToAddCharacter = { storyId, title, charId -> navController.navigateSingleTopTo("${AddEditCharacter.route}/${storyId}/${title}?${charId}") },
                    navToCharacterDetails = { storyId, charName -> navController.navigateSingleTopTo("${CharacterDetails.route}/$storyId/$charName")},
                    navToEditStory = {storyId -> navController.navigateSingleTopTo("${AddEditStory.route}?$storyId")},
                    onBackNav = { navController.navigateUp() },
                    storyTitle = storyTitle
                )
            }
        }
        composable(route= CharacterDetails.routeWithArgs,
            arguments = CharacterDetails.arguments){
            navBackStackEntry ->
            val storyId = navBackStackEntry.arguments?.getString(CharacterDetails.storyIdArg)
            val charName = navBackStackEntry.arguments?.getString(CharacterDetails.charNameArg)
            if (storyId != null && charName != null) {
                CharacterDetailsScreen(
                    onBackNav = { navController.navigateUp() },
                    storyId = storyId,
                    charName = charName)
            }

        }
        composable(route= AddEditCharacter.routeWithArgs,
            arguments = AddEditCharacter.arguments){
            navBackStackEntry ->
            val storyId = navBackStackEntry.arguments?.getString(AddEditCharacter.storyIdArg)
            val storyTitle = navBackStackEntry.arguments?.getString(AddEditCharacter.storyTitleArg)
            val charId = navBackStackEntry.arguments?.getString(AddEditCharacter.charIdArg)
            if (storyId != null && storyTitle != null){
                AddEditCharacterScreen(
                    storyId = storyId,
                    storyTitle = storyTitle,
                    charId = charId,
                    onBackNav = { navController.navigateUp() })
            }

        }
    }
}

//navToEditStory = {storyId -> navController.navigateSingleTopTo("${AddEditStory.route}/$storyId") },

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) { launchSingleTop = true }