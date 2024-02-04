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
    const val storyTitleArg = "story_title"
    val routeWithArgs = "${route}/{${storyTitleArg}}"
    val arguments = listOf(navArgument(storyTitleArg) { type = NavType.StringType })
}

object CharacterDetails: Destination{
    override val route = "characterDetails"
    const val storyIdArg = "story_id"
    const val charNameArg = "char_name"
    val routeWithArgs = "${route}/{${storyIdArg}}/{${charNameArg}}"
    val arguments = listOf(
        navArgument(storyIdArg, builder = {type = NavType.StringType}),
        navArgument(charNameArg, builder = {type = NavType.StringType})
    )

}

// list of all screens for use to determine which screen currently at
val charTrackerScreens = listOf(SignIn, SignUp, EmailVerify, Stories, AddEditStory, Characters, CharacterDetails)