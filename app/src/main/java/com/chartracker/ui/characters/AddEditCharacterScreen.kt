package com.chartracker.ui.characters

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.R
import com.chartracker.database.CharacterEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun AddEditCharacterScreen(
    onBackNav: () -> Unit,
){

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddEditCharacterScreen(
    name: String,
    onNameChange: (String) -> Unit,
    aliases: String,
    onAliasesChange: (String) -> Unit,
    titles: String,
    onTitlesChange: (String) -> Unit,
    age: String,
    onAgeChange: (Int) -> Unit,
    home: String,
    onHomeChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    race: String,
    onRaceChange: (String) -> Unit,
    livingOrDead: String,
    onLivingOrDeadChange: (String) -> Unit,
    occupation: String,
    onOccupationChange: (String) -> Unit,
    weapons: String,
    onWeaponsChange: (String) -> Unit,
    toolsEquipment: String,
    onToolsEquipmentChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    faction: String,
    onFactionChange: (String) -> Unit,
    allies: List<String>,
    onAlliesChange: (List<String>) -> Unit,
    enemies: List<String>,
    onEnemiesChange: (List<String>) -> Unit,
    neutral: List<String>,
    onNeutralChange: (List<String>) -> Unit,
    submitCharacter: (CharacterEntity, Uri?) -> Unit,
    readyToNavToCharacters: Boolean,
    resetNavToCharacters: () -> Unit,
    startImage: Uri?,
    onBackNav: () -> Unit,
){
    if (readyToNavToCharacters){
        resetNavToCharacters()
        onBackNav()
    }
    val localUri = remember { mutableStateOf(startImage) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia() ){
        localUri.value = it
    }

    Scaffold(topBar = { CharTrackerTopBar(onBackNav=onBackNav) {} })  { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "AddEditStory Screen" }){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (localUri.value != null){
                        GlideImage(
                            model = localUri.value,
                            contentDescription = stringResource(id = R.string.story_image_desc),
                            loading = placeholder(R.drawable.baseline_downloading_24),
                            failure = placeholder(R.drawable.baseline_broken_image_24),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape))
                        Button(onClick = {localUri.value = null}) {
                            Text(text = stringResource(id = R.string.remove_selected_image))
                        }
                    }else{
                        Spacer(modifier = Modifier
                            .size(120.dp))
                        Button(onClick = {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Text(text = stringResource(id = R.string.select_image))
                        }
                    }
                }
            }
            TextEntryHolder(
                title = R.string.name,
                label = R.string.name_hint,
                text = name,
                onTyping = {newInput -> onNameChange(newInput)})
            TextEntryHolder(
                title = R.string.aliases,
                label = R.string.aliases_hint,
                text = aliases,
                onTyping = {newInput -> onAliasesChange(newInput)})
            TextEntryHolder(
                title = R.string.titles,
                label = R.string.titles_hint,
                text = titles,
                onTyping = {newInput -> onTitlesChange(newInput)})
            TextEntryHolder(
                title = R.string.age,
                label = R.string.age_hint,
                text = age,
                onTyping = {newInput -> onAgeChange(newInput.toInt())})
            TextEntryHolder(
                title = R.string.home,
                label = R.string.home_hint,
                text = home,
                onTyping = {newInput -> onHomeChange(newInput)})
            TextEntryHolder(
                title = R.string.gender,
                label = R.string.gender_hint,
                text = gender,
                onTyping = {newInput -> onGenderChange(newInput)})
            TextEntryHolder(
                title = R.string.race,
                label = R.string.race_hint,
                text = race,
                onTyping = {newInput -> onRaceChange(newInput)})
            TextEntryHolder(
                title = R.string.living_dead,
                label = R.string.living_dead_hint,
                text = livingOrDead,
                onTyping = {newInput -> onLivingOrDeadChange(newInput)})
            TextEntryHolder(
                title = R.string.occupation,
                label = R.string.occupation_hint,
                text = occupation,
                onTyping = {newInput -> onOccupationChange(newInput)})
            TextEntryHolder(
                title = R.string.weapon,
                label = R.string.weapon_hint,
                text = weapons,
                onTyping = {newInput -> onWeaponsChange(newInput)})
            TextEntryHolder(
                title = R.string.tools_equipment,
                label = R.string.tools_equipment_hint,
                text = toolsEquipment,
                onTyping = {newInput -> onToolsEquipmentChange(newInput)})
            TextEntryHolder(
                title = R.string.bio,
                label = R.string.bio_hint,
                text = titles,
                onTyping = {newInput -> onBioChange(newInput)})
            TextEntryHolder(
                title = R.string.faction,
                label = R.string.faction_hint,
                text = faction,
                onTyping = {newInput -> onFactionChange(newInput)})
            //TODO figure out chips
            Button(onClick = {
                if (name != "") {
                    val character = CharacterEntity(
                        name = name,
                        aliases = aliases,
                        titles = titles,
                        age = age.toInt(),
                        home = home,
                        gender = gender,
                        race = race,
                        livingOrDead = livingOrDead,
                        occupation = occupation,
                        weapons = weapons,
                        toolsEquipment = toolsEquipment,
                        bio = bio,
                        faction = faction
                    )
                    submitCharacter(character, localUri.value)
                }
            }) {
                Text(text = stringResource(id = R.string.submit))
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
fun PreviewAddCharacterScreen(){
    var name by remember { mutableStateOf("") }
    var aliases by remember { mutableStateOf("") }
    var titles by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var home by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var race by remember { mutableStateOf("") }
    var livingOrDead by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var weapons by remember { mutableStateOf("") }
    var toolsEquipment by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var faction by remember { mutableStateOf("") }
    var allies by remember { mutableStateOf(listOf("")) }
    var enemies by remember { mutableStateOf(listOf("")) }
    var neutral by remember { mutableStateOf(listOf("")) }

    CharTrackerTheme {
        Surface {
            AddEditCharacterScreen(
                name = name,
                onNameChange = {newInput:String -> name = newInput},
                aliases = aliases,
                onAliasesChange = {newInput:String  -> aliases = newInput},
                titles = titles,
                onTitlesChange = {newInput:String  -> titles = newInput},
                age = age,
                onAgeChange = {newInput:Int  -> age = newInput.toString()},
                home = home,
                onHomeChange = {newInput:String  -> home = newInput},
                gender = gender,
                onGenderChange = {newInput:String  -> gender = newInput},
                race = race,
                onRaceChange = {newInput:String  -> race = newInput},
                livingOrDead = livingOrDead,
                onLivingOrDeadChange = {newInput:String  -> livingOrDead = newInput},
                occupation = occupation,
                onOccupationChange = {newInput:String  -> occupation = newInput},
                weapons = weapons,
                onWeaponsChange = {newInput:String  -> weapons = newInput},
                toolsEquipment = toolsEquipment,
                onToolsEquipmentChange = {newInput:String  -> toolsEquipment = newInput},
                bio = bio,
                onBioChange = {newInput:String  -> bio = newInput},
                faction = faction,
                onFactionChange = {newInput:String  -> faction = newInput},
                allies = allies,
                onAlliesChange = {newInput:List<String>  -> allies = newInput},
                enemies = enemies,
                onEnemiesChange = {newInput:List<String> -> enemies = newInput},
                neutral = neutral,
                onNeutralChange = {newInput:List<String> -> neutral = newInput},
                submitCharacter = {_, _ ->},
                readyToNavToCharacters = false,
                resetNavToCharacters = { /*TODO*/ },
                startImage = null
            ) {

            }
        }
    }
}