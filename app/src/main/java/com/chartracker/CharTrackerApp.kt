package com.chartracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chartracker.ui.auth.SignInScreen
import com.chartracker.ui.auth.SignUpScreen
import com.chartracker.ui.theme.CharTrackerTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.chartracker.ui.auth.EmailVerifyScreen

@Composable
fun CharTrackerApp(){
    CharTrackerTheme {
        var currentScreen: Destination by remember { mutableStateOf(SignIn) }
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = SignIn.route) {
            composable(route = SignIn.route){
                SignInScreen(navToSignUp = { navController.navigate(SignUp.route) })
            }
            composable(route = SignUp.route){
                SignUpScreen(navToEmailVerify = { navController.navigate(EmailVerify.route) })
            }
            composable(route= EmailVerify.route){
                EmailVerifyScreen(
                    navToUpdateEmail = { /*TODO*/ },
                    navToStories = { /*TODO*/ })
            }
        }
    }
}