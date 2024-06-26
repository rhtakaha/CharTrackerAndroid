package com.chartracker.ui.characters

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import com.chartracker.ui.components.AdmobBanner
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.RefreshDialog
import com.chartracker.ui.components.TextAndContentHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.characters.CharacterDetailsViewModel
import com.chartracker.viewmodels.characters.CharacterDetailsViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun CharacterDetailsScreen(
    navToEditCharacter: (String, String, String) -> Unit,
    onBackNav: () -> Unit,
    storyId: String,
    storyTitle: String,
    charName: String,
    characterDB: CharacterDBInterface,
    characterDetailsViewModel: CharacterDetailsViewModel = viewModel(
        factory = CharacterDetailsViewModelFactory(
            storyId = storyId,
            charName = charName,
            characterDB = characterDB
        )
    )
){
    CharacterDetailsScreen(
        character = characterDetailsViewModel.character.value,
        failedGetCharacter = characterDetailsViewModel.failedGetCharacter.value,
        resetFailedGetCharacter = { characterDetailsViewModel.resetFailedGetCharacter() },
        refresh = { characterDetailsViewModel.setup() },
        alliesList = characterDetailsViewModel.alliesList,
        enemiesList = characterDetailsViewModel.enemiesList,
        neutralList = characterDetailsViewModel.neutralList,
        factionList = characterDetailsViewModel.factionsList,
        navToEditCharacter={navToEditCharacter(storyId, storyTitle, charName)},
        onBackNav = onBackNav)
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun CharacterDetailsScreen(
    character: CharacterEntity,
    failedGetCharacter: Boolean,
    resetFailedGetCharacter: () -> Unit,
    refresh: () -> Unit,
    alliesList: String?,
    enemiesList: String?,
    neutralList: String?,
    factionList: String?,
    navToEditCharacter: () -> Unit,
    onBackNav: () -> Unit
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CharTrackerTopBar(
                title =  character.name.value,
                onBackNav = onBackNav,
                actionButtons = {
                    IconButton(onClick = { navToEditCharacter() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(id = R.string.edit_character)
                        )
                    }
                })
        },
        bottomBar = {
            AdmobBanner()
        },
        modifier = Modifier
            .navigationBarsPadding()
    ) {paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(paddingValue)
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "CharacterDetails Screen" }
        ) {
            if (character.imagePublicUrl.value != null){
                GlideImage(
                    model = character.imagePublicUrl.value,
                    contentDescription = stringResource(id = R.string.character_image_desc),
                    loading = placeholder(R.drawable.baseline_downloading_24),
                    failure = placeholder(R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            TextAndContentHolder(
                title = R.string.name,
                body = character.name.value
            )
            if (character.aliases.value != ""){
                TextAndContentHolder(
                    title = R.string.aliases,
                    body = character.aliases.value
                )
            }
            if (character.titles.value != ""){
                TextAndContentHolder(
                    title = R.string.titles,
                    body = character.titles.value
                )
            }
            if (character.age.value != ""){
                TextAndContentHolder(
                    title = R.string.age,
                    body = character.age.value
                )
            }
            if (character.home.value != ""){
                TextAndContentHolder(
                    title = R.string.home,
                    body = character.home.value
                )
            }
            if (character.gender.value != ""){
                TextAndContentHolder(
                    title = R.string.gender,
                    body = character.gender.value
                )
            }
            if (character.race.value != ""){
                TextAndContentHolder(
                    title = R.string.race,
                    body = character.race.value
                )
            }
            if (character.livingOrDead.value != ""){
                TextAndContentHolder(
                    title = R.string.living_dead,
                    body = character.livingOrDead.value
                )
            }
            if (character.occupation.value != ""){
                TextAndContentHolder(
                    title = R.string.occupation,
                    body = character.occupation.value
                )
            }
            if (character.weapons.value != ""){
                TextAndContentHolder(
                    title = R.string.weapon,
                    body = character.weapons.value
                )
            }
            if (character.toolsEquipment.value != ""){
                TextAndContentHolder(
                    title = R.string.tools_equipment,
                    body = character.toolsEquipment.value
                )
            }
            if (character.bio.value != ""){
                TextAndContentHolder(
                    title = R.string.bio,
                    body = character.bio.value
                )
            }
            if (!factionList.isNullOrEmpty()){
                TextAndContentHolder(
                    title = R.string.faction,
                    body = factionList
                )
            }
            if (!alliesList.isNullOrEmpty()){
                TextAndContentHolder(
                    title = R.string.allies,
                    body = alliesList
                )
            }
            if (!enemiesList.isNullOrEmpty()){
                TextAndContentHolder(
                    title = R.string.enemies,
                    body = enemiesList
                )
            }
            if (!neutralList.isNullOrEmpty()){
                TextAndContentHolder(
                    title = R.string.neutral,
                    body = neutralList
                )
            }
        }
        if (failedGetCharacter){
            RefreshDialog(
                message = stringResource(id = R.string.failed_get_character),
                refresh = {
                    refresh()
                    resetFailedGetCharacter()
                          },
                onDismiss = { resetFailedGetCharacter()})
        }
        ConnectivityStatus(scope, snackbarHostState)
    }
}



@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewCharacterDetailsScreen(){
    CharTrackerTheme {
        Surface {
            val character = CharacterEntity(
                name = "Aragorn",
                aliases = "Strider",
                titles = "King of Gondor and Arnor",
                age = "80",
                home = "Rivendell (Imladris)",
                gender = "male",
                race = "man/Númenórean",
                livingOrDead = "living",
                occupation = "Ranger then King",
                weapons = "Andúril",
                toolsEquipment = "",
                bio = "The ranger from the North who would be king and unite the kingdoms",
                faction = listOf("Men of the West"),
                allies = listOf("Frodo Baggins", "Gandalf", "Gimli", "Legolas", "Farimir"),
                enemies = listOf("Sauron", "Sauruman", "Corsairs of Umbar"),
                neutral = listOf("Someone"),
                imageFilename = null,
                imagePublicUrl = null
                )
            CharacterDetailsScreen(
                character = character,
                failedGetCharacter = false,
                resetFailedGetCharacter = {},
                refresh = {},
                alliesList = "Frodo Baggins, Gandalf, Gimli, Legolas, Farimir",
                enemiesList = "Sauron, Sauruman, Corsairs of Umbar",
                neutralList = null,
                factionList = "Men of the West",
                navToEditCharacter = {},
                onBackNav = {}
                )
        }
    }
}