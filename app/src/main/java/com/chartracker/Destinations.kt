package com.chartracker

import androidx.navigation.NavType
import androidx.navigation.navArgument


interface Destination {
    val route: String
}

object SignIn : Destination{
    override val route = "signIn"
}

object SignUp : Destination{
    override val route = "signUp"
}

object EmailVerify : Destination{
    override val route = "verifyEmail"
    const val userEmailArg = "user_email"
    val routeWithArgs = "${route}/{${userEmailArg}}"
    val arguments = listOf(navArgument(userEmailArg) { type = NavType.StringType})
}

object Stories: Destination{
    override val route = "stories"
}

object AddEditStory: Destination{
    override val route = "addEditStory"
    const val storyIdArg = "story_id"
    val routeWithArgs = "${route}?{${storyIdArg}}"
    val arguments = listOf(navArgument(storyIdArg) {
        defaultValue = null
        nullable = true
        type = NavType.StringType
    })
}

object Characters: Destination{
    override val route = "characters"
    const val charactersIdArg = "story_title"
    val routeWithArgs = "${route}/{${charactersIdArg}}"
    val arguments = listOf(navArgument(charactersIdArg) { type = NavType.StringType })
}

// list of all screens for use to determine which screen currently at
val charTrackerScreens = listOf(SignIn, SignUp, EmailVerify, Stories, AddEditStory, Characters)