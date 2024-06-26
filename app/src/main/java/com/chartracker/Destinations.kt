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

object Factions: Destination{
    override val route = "factions"
    const val storyIdArg = "story_id"
    const val storyTitleArg = "story_title"
    val routeWithArgs = "${route}/{${storyIdArg}}/{${storyTitleArg}}"
    val arguments = listOf(
        navArgument(storyIdArg, builder = {type = NavType.StringType}),
        navArgument(storyTitleArg, builder = {type = NavType.StringType})
    )
}

object CharacterDetails: Destination{
    override val route = "characterDetails"
    const val storyIdArg = "story_id"
    const val storyTitleArg = "story_title"
    const val charNameArg = "char_name"
    val routeWithArgs = "${route}/{${storyIdArg}}/{${storyTitleArg}}/{${charNameArg}}"
    val arguments = listOf(
        navArgument(storyIdArg, builder = {type = NavType.StringType}),
        navArgument(storyTitleArg, builder = {type = NavType.StringType}),
        navArgument(charNameArg, builder = {type = NavType.StringType})
    )
}

object AddEditCharacter: Destination{
    override val route = "addEditCharacter"
    const val storyIdArg = "story_id"
    const val storyTitleArg = "story_title"
    const val charNameArg = "character_name"
    val routeWithArgs = "$route/{$storyIdArg}/{$storyTitleArg}?{$charNameArg}"
    val arguments = listOf(
        navArgument(storyIdArg, builder = {type = NavType.StringType}),
        navArgument(storyTitleArg, builder = {type = NavType.StringType}),
        navArgument(charNameArg, builder = {
            defaultValue = null
            nullable = true
            type = NavType.StringType
        }),
    )
}

object Settings: Destination{
    override val route = "settings"
}

// list of all screens for use to determine which screen currently at
val charTrackerScreens = listOf(
    SignIn,
    SignUp,
    EmailVerify,
    Stories,
    AddEditStory,
    Characters,
    Factions,
    CharacterDetails,
    AddEditCharacter,
    Settings
)