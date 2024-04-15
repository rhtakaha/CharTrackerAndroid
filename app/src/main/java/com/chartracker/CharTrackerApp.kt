package com.chartracker

import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chartracker.ui.auth.SignInScreen
import com.chartracker.ui.auth.SignUpScreen
import com.chartracker.ui.theme.CharTrackerTheme
import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
import com.chartracker.database.CharacterDB
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.ImageDB
import com.chartracker.database.ImageDBInterface
import com.chartracker.database.StoryDB
import com.chartracker.database.StoryDBInterface
import com.chartracker.database.UserDB
import com.chartracker.database.UserDBInterface
import com.chartracker.ui.auth.EmailVerifyScreen
import com.chartracker.ui.auth.SettingsScreen
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
//        val currentBackStack by navController.currentBackStackEntryAsState()
//        val currentDestination = currentBackStack?.destination
//        val currentScreen =
//            charTrackerScreens.find { it.route == currentDestination?.route } ?: SignIn
        CharTrackerNavHost(
            navController = navController,
            userDB = UserDB(),
            storyDB = StoryDB(),
            imageDB = ImageDB(),
            characterDB = CharacterDB()
        )
    }
}

@Composable
fun CharTrackerNavHost(
    navController: NavHostController,
    userDB: UserDBInterface,
    storyDB: StoryDBInterface,
    imageDB: ImageDBInterface,
    characterDB: CharacterDBInterface
){
    NavHost(navController = navController, startDestination = SignIn.route) {
        composable(route = SignIn.route){
            SignInScreen(
                userDB = userDB,
                navToSignUp = { navController.navigateSingleTopTo(SignUp.route) },
                navToStories = {navController.navigateSingleTopTo(Stories.route)})
        }
        composable(route = SignUp.route){
            SignUpScreen(
                userDB = userDB,
                navToEmailVerify = {email -> navController.navigateSingleTopToNoReturn("${EmailVerify.route}/$email") },
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
                navToStories = {navController.navigateSingleTopToNoReturn(Stories.route)},
                userEmail = userEmail,
                userDB = userDB
            )
        }
        composable(route = Stories.route){
            StoriesScreen(
                storyDB = storyDB,
                navToAddStory = { navController.navigateSingleTopTo(AddEditStory.route) },
                navToCharacters = { storyTitle -> navController.navigateSingleTopTo("${Characters.route}/$storyTitle") },
                navToSettings = {navController.navigateSingleTopTo(Settings.route)}
            )
        }
        composable(
            route= AddEditStory.routeWithArgs,
            arguments = AddEditStory.arguments
        ){
            navBackStackEntry ->
            val storyId = navBackStackEntry.arguments?.getString(AddEditStory.storyIdArg)
            AddEditStoryScreen(
                navToStories = {navController.navigateSingleTopToNoReturn(Stories.route)},
                onBackNav = {navController.navigateUp()},
                storyId = storyId,
                storyDB = storyDB,
                imageDB = imageDB
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
                    navToAddCharacter = { storyId, title, charName -> navController.navigateSingleTopTo("${AddEditCharacter.route}/${storyId}/${title}?${charName}") },
                    navToCharacterDetails = { storyId, title, charName -> navController.navigateSingleTopTo("${CharacterDetails.route}/$storyId/$title/$charName")},
                    navToEditStory = {storyId -> navController.navigateSingleTopTo("${AddEditStory.route}?$storyId")},
                    navToSettings = {navController.navigateSingleTopTo(Settings.route)},
                    onBackNav = { navController.navigateUp() },
                    storyTitle = storyTitle,
                    storyDB = storyDB,
                    characterDB = characterDB
                )
            }
        }
        composable(route= CharacterDetails.routeWithArgs,
            arguments = CharacterDetails.arguments){
            navBackStackEntry ->
            val storyId = navBackStackEntry.arguments?.getString(CharacterDetails.storyIdArg)
            val storyTitle = navBackStackEntry.arguments?.getString(CharacterDetails.storyTitleArg)
            val charName = navBackStackEntry.arguments?.getString(CharacterDetails.charNameArg)
            if (storyId != null && storyTitle != null && charName != null) {
                CharacterDetailsScreen(
                    navToEditCharacter = { id, title, name -> navController.navigateSingleTopTo("${AddEditCharacter.route}/${id}/${title}?${name}")},
                    onBackNav = { navController.navigateUp() },
                    storyId = storyId,
                    storyTitle = storyTitle,
                    charName = charName,
                    characterDB = characterDB
                )
            }

        }
        composable(route= AddEditCharacter.routeWithArgs,
            arguments = AddEditCharacter.arguments){
            navBackStackEntry ->
            val storyId = navBackStackEntry.arguments?.getString(AddEditCharacter.storyIdArg)
            val storyTitle = navBackStackEntry.arguments?.getString(AddEditCharacter.storyTitleArg)
            val charName = navBackStackEntry.arguments?.getString(AddEditCharacter.charNameArg)
            if (storyId != null && storyTitle != null){
                AddEditCharacterScreen(
                    storyId = storyId,
                    storyTitle = storyTitle,
                    charName = charName,
                    imageDB = imageDB,
                    characterDB = characterDB,
                    navToCharacters = { navController.navigateSingleTopToNoReturn("${Characters.route}/$storyTitle") },
                    onBackNav = { navController.navigateUp() }
                )
            }
        }
        composable(route= Settings.route){
            SettingsScreen(
                userDB = userDB,
                navToSignIn = { navController.navigateSingleTopToNoReturn(SignIn.route) },
                onBackNav = { navController.navigateUp() }
            )
        }
    }
}

//navToEditStory = {storyId -> navController.navigateSingleTopTo("${AddEditStory.route}/$storyId") },

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) { launchSingleTop = true }

fun NavHostController.navigateSingleTopToNoReturn(route: String) = this.navigate(route) {
    popBackStack()
    launchSingleTop = true
}