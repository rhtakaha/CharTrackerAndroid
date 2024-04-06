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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import com.chartracker.database.ImageDBInterface
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.ChipGroupRow
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.characters.AddEditCharacterViewModel
import com.chartracker.viewmodels.characters.AddEditCharacterViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun AddEditCharacterScreen(
    storyId: String,
    storyTitle: String,
    charName: String?,
    imageDB: ImageDBInterface,
    characterDB: CharacterDBInterface,
    navToCharacters: () -> Unit,
    onBackNav: () -> Unit,
    addEditCharacterViewModel: AddEditCharacterViewModel =
        viewModel(
            factory = AddEditCharacterViewModelFactory(
                storyId = storyId,
                storyTitle = storyTitle,
                charName = charName,
                imageDB = imageDB,
                characterDB = characterDB
            )
        )
){
    AddEditCharacterScreen(
        character = addEditCharacterViewModel.character.value,
        editing = charName != null,
        charactersStringList = addEditCharacterViewModel.charactersStringList,
        updateAllies = { name, selected -> addEditCharacterViewModel.alliesUpdated(name, selected) },
        updateEnemies = { name, selected -> addEditCharacterViewModel.enemiesUpdated(name, selected) },
        updateNeutrals = { name, selected -> addEditCharacterViewModel.neutralsUpdated(name, selected) },
        submitCharacter = {character: CharacterEntity, localUri: Uri? -> addEditCharacterViewModel.submitCharacter(character, localUri)},
        deleteCharacter = { addEditCharacterViewModel.submitCharacterDelete() },
        uploadError = addEditCharacterViewModel.uploadError.value,
        resetUploadError = { addEditCharacterViewModel.resetUploadError() },
        retrievalError = addEditCharacterViewModel.retrievalError.value,
        resetRetrievalError = { addEditCharacterViewModel.resetRetrievalError() },
        duplicateNameError = addEditCharacterViewModel.duplicateNameError.value,
        resetDuplicateNameError = { addEditCharacterViewModel.resetDuplicateNameError()},
        readyToNavToCharacters = addEditCharacterViewModel.readyToNavToCharacters.value,
        navToCharacters = navToCharacters,
        resetNavToCharacters = { addEditCharacterViewModel.resetReadyToNavToCharacters() },
        startImage = addEditCharacterViewModel.character.value.imagePublicUrl.value?.toUri(),
        onBackNav = onBackNav
    )
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun AddEditCharacterScreen(
    character: CharacterEntity,
    editing: Boolean = false,
    charactersStringList: List<String>,
    updateAllies: (String, Boolean) -> Unit,
    updateEnemies: (String, Boolean) -> Unit,
    updateNeutrals: (String, Boolean) -> Unit,
    submitCharacter: (CharacterEntity, Uri?) -> Unit,
    deleteCharacter: () -> Unit,
    uploadError: Boolean,
    resetUploadError: () -> Unit,
    retrievalError: Boolean,
    resetRetrievalError: () -> Unit,
    duplicateNameError: Boolean,
    resetDuplicateNameError: () -> Unit,
    readyToNavToCharacters: Boolean,
    navToCharacters: () -> Unit,
    resetNavToCharacters: () -> Unit,
    startImage: Uri?,
    onBackNav: () -> Unit,
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    if (readyToNavToCharacters){
        resetNavToCharacters()
        navToCharacters()
    }
    val localUri = remember(key1 = startImage) {
        mutableStateOf(startImage)
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia() ){
        localUri.value = it
    }

    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
        CharTrackerTopBar(onBackNav=onBackNav, actionButtons = {
            IconButton(onClick = {
                if (character.name.value != "") {
                    submitCharacter(character, localUri.value)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(id = R.string.submit_char)
                )
            }
            if (editing){
                IconButton(onClick = { deleteCharacter() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }
            }
        })
    })  { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(paddingValue)
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "AddEditCharacter Screen" }){
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
                text = character.name.value,
                onTyping = {newInput -> character.name.value = newInput})
            TextEntryHolder(
                title = R.string.aliases,
                label = R.string.aliases_hint,
                text = character.aliases.value,
                onTyping = {newInput -> character.aliases.value = newInput})
            TextEntryHolder(
                title = R.string.titles,
                label = R.string.titles_hint,
                text = character.titles.value,
                onTyping = {newInput -> character.titles.value = newInput})
            TextEntryHolder(
                title = R.string.age,
                label = R.string.age_hint,
                text = character.age.value,
                onTyping = {newInput -> character.age.value = newInput})
            TextEntryHolder(
                title = R.string.home,
                label = R.string.home_hint,
                text = character.home.value,
                onTyping = {newInput -> character.home.value = newInput})
            TextEntryHolder(
                title = R.string.gender,
                label = R.string.gender_hint,
                text = character.gender.value,
                onTyping = {newInput -> character.gender.value = newInput})
            TextEntryHolder(
                title = R.string.race,
                label = R.string.race_hint,
                text = character.race.value,
                onTyping = {newInput -> character.race.value = newInput})
            TextEntryHolder(
                title = R.string.living_dead,
                label = R.string.living_dead_hint,
                text = character.livingOrDead.value,
                onTyping = {newInput ->  character.livingOrDead.value = newInput})
            TextEntryHolder(
                title = R.string.occupation,
                label = R.string.occupation_hint,
                text = character.occupation.value,
                onTyping = {newInput -> character.occupation.value = newInput})
            TextEntryHolder(
                title = R.string.weapon,
                label = R.string.weapon_hint,
                text = character.weapons.value,
                onTyping = {newInput -> character.weapons.value = newInput})
            TextEntryHolder(
                title = R.string.tools_equipment,
                label = R.string.tools_equipment_hint,
                text = character.toolsEquipment.value,
                onTyping = {newInput -> character.toolsEquipment.value = newInput})
            TextEntryHolder(
                title = R.string.bio,
                label = R.string.bio_hint,
                text = character.bio.value,
                onTyping = {newInput -> character.bio.value = newInput})
            TextEntryHolder(
                title = R.string.faction,
                label = R.string.faction_hint,
                text = character.faction.value,
                onTyping = {newInput -> character.faction.value = newInput})
            ChipGroupRow(
                header = stringResource(id = R.string.allies),
                contentsList = charactersStringList,
                onClick = updateAllies
            )
            ChipGroupRow(
                header = stringResource(id = R.string.enemies),
                contentsList = charactersStringList,
                onClick = updateEnemies
            )
            ChipGroupRow(
                header = stringResource(id = R.string.neutral),
                contentsList = charactersStringList,
                onClick = updateNeutrals
            )

        }
        if (uploadError){
            MessageDialog(
                message = stringResource(id = R.string.char_upload_error),
                onDismiss = {resetUploadError()}
            )
        }
        if (retrievalError){
            MessageDialog(
                message = stringResource(id = R.string.char_retrieval_error),
                onDismiss = {resetRetrievalError()}
            )
        }
        if (duplicateNameError){
            MessageDialog(
                message = stringResource(id = R.string.duplicate_name_error),
                onDismiss = {resetDuplicateNameError()}
            )
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
fun PreviewAddCharacterScreen(){
    val character by remember {
        mutableStateOf(CharacterEntity())
    }

    val charactersList = listOf("Gandalf", "Aragorn", "Frodo Baggins")
    CharTrackerTheme {
        Surface {
            AddEditCharacterScreen(
                character = character,
                charactersStringList = charactersList,
                updateAllies = {_, _ ->},
                updateEnemies = {_, _ ->},
                updateNeutrals = {_, _ ->},
                submitCharacter = {_, _ ->},
                deleteCharacter = {},
                uploadError = false,
                resetUploadError = {},
                retrievalError = false,
                resetRetrievalError = {},
                duplicateNameError = false,
                resetDuplicateNameError = {},
                readyToNavToCharacters = false,
                navToCharacters = {},
                resetNavToCharacters = { /*TODO*/ },
                startImage = null
            ) {

            }
        }
    }
}