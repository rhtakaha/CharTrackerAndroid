package com.chartracker.ui.characters

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.R
import com.chartracker.database.CharacterEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.TextAndContentHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.characters.CharacterDetailsViewModel
import com.chartracker.viewmodels.characters.CharacterDetailsViewModelFactory

@Composable
fun CharacterDetailsScreen(
    onBackNav: () -> Unit,
    storyId: String,
    charName: String,
    characterDetailsViewModel: CharacterDetailsViewModel = viewModel(
        factory = CharacterDetailsViewModelFactory(storyId, charName))
){
    CharacterDetailsScreen(
        character = characterDetailsViewModel.character.value,
        onBackNav = onBackNav)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CharacterDetailsScreen(
    character: CharacterEntity,
    onBackNav: () -> Unit
){
    Scaffold(
        topBar = {
            CharTrackerTopBar(
                title =  character.name.value,
                onBackNav = onBackNav,
                actionButtons = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(id = R.string.edit_character)
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                })
        },
    ) {paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(paddingValue)
        ) {
            character.imagePublicUrl.value.let {
                GlideImage(
                    model = character.imagePublicUrl.value,
                    contentDescription = null,
                    loading = placeholder(R.drawable.baseline_downloading_24),
                    failure = placeholder(R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            character.name.value.let {
                TextAndContentHolder(
                    title = R.string.name,
                    body = it
                )
            }
            character.aliases?.let {
                TextAndContentHolder(
                    title = R.string.aliases,
                    body = it
                )
            }
            character.titles?.let {
                TextAndContentHolder(
                    title = R.string.titles,
                    body = it
                )
            }
            character.age?.let {
                TextAndContentHolder(
                    title = R.string.age,
                    body = it.toString()
                )
            }
            character.home?.let {
                TextAndContentHolder(
                    title = R.string.home,
                    body = it
                )
            }
            character.gender?.let {
                TextAndContentHolder(
                    title = R.string.gender,
                    body = it
                )
            }
            character.race?.let {
                TextAndContentHolder(
                    title = R.string.race,
                    body = it
                )
            }
            character.livingOrDead?.let {
                TextAndContentHolder(
                    title = R.string.living_dead,
                    body = it
                )
            }
            character.occupation?.let {
                TextAndContentHolder(
                    title = R.string.occupation,
                    body = it
                )
            }
            character.weapons?.let {
                TextAndContentHolder(
                    title = R.string.weapon,
                    body = it
                )
            }
            character.toolsEquipment?.let {
                TextAndContentHolder(
                    title = R.string.tools_equipment,
                    body = it
                )
            }
            character.bio?.let {
                TextAndContentHolder(
                    title = R.string.bio,
                    body = it
                )
            }
            character.faction?.let {
                TextAndContentHolder(
                    title = R.string.faction,
                    body = it
                )
            }
            character.allies?.let {
                TextAndContentHolder(
                    title = R.string.name,
                    body = it.toString()
                )
            }
            character.enemies?.let {
                TextAndContentHolder(
                    title = R.string.name,
                    body = it.toString()
                )
            }
            character.neutral?.let {
                TextAndContentHolder(
                    title = R.string.name,
                    body = it.toString()
                )
            }
        }
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
                age = 80,
                home = "Rivendell (Imladris)",
                gender = "male",
                race = "man/Númenórean",
                livingOrDead = "living",
                occupation = "Ranger then King",
                weapons = "Andúril",
                toolsEquipment = null,
                bio = "The ranger from the North who would be king and unite the kingdoms",
                faction = "Men of the West",
                allies = listOf("Frodo Baggins, Gandalf, Gimli, Legolass", "Farimir"),
                enemies = listOf("Sauron, Sauruman, Corsairs of Umbar"),
                neutral = listOf("Someone"),
                imageFilename = "",
                imagePublicUrl = ""
                )
            CharacterDetailsScreen(
                character = character,
                onBackNav = {}
                )
        }
    }
}