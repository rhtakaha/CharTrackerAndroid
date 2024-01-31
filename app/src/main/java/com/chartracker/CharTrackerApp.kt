package com.chartracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chartracker.ui.auth.SignInScreen
import com.chartracker.ui.auth.SignUpScreen
import com.chartracker.ui.theme.CharTrackerTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chartracker.ui.auth.EmailVerifyScreen
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
    navController: NavHostController,
    modifier: Modifier = Modifier
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
        ){ navBackStackEntry ->
            val userEmail = navBackStackEntry.arguments?.getString(EmailVerify.userEmailArg) ?: ""
            EmailVerifyScreen(
                navToUpdateEmail = { /*TODO*/ },
                navToStories = { /*TODO*/ },
                userEmail = userEmail,
                onBackNav = {navController.navigateUp()})
        }
        composable(route = Stories.route){
            StoriesScreen(
                onAddEditStoryNav = { navController.navigateSingleTopTo(AddEditStory.route) },
                onBackNav = {navController.navigateUp()})
        }
        composable(route= AddEditStory.route){
            AddEditStoryScreen(onBackNav = {navController.navigateUp()})
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) { launchSingleTop = true }