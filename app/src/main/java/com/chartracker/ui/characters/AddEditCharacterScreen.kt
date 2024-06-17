package com.chartracker.ui.characters

import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import com.chartracker.database.ImageDBInterface
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.ChipGroupRow
import com.chartracker.ui.components.ConfirmDialog
import com.chartracker.ui.components.FactionsChipGroup
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.characters.AddEditCharacterViewModel
import com.chartracker.viewmodels.characters.AddEditCharacterViewModelFactory
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

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
        currentFactions = addEditCharacterViewModel.currentFactions,
        updateFactions = {name: String, selected: Boolean -> addEditCharacterViewModel.factionsUpdated(name, selected)},
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
    currentFactions: Map<String, Long>,
    updateFactions: (String, Boolean) -> Unit,
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

    val croppingError = remember {
        mutableStateOf(false)
    }

    val croppingInProgress = remember {
        mutableStateOf(false)
    }

    var confirmBack by remember {
        mutableStateOf(false)
    }

    var confirmDelete by remember {
        mutableStateOf(false)
    }

    BackHandler {
        confirmBack = true
    }

    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        croppingInProgress.value = true
        if (result.isSuccessful) {
            // use the cropped image
            val path = result.getUriFilePath(context = context)

            Timber.tag("Add Image").i("original cropped uri: ${result.uriContent} and path: $path")

            if (path != null) {
                scope.launch {
                    val file =  File(path)
                    Timber.tag("Add Image").i("starting compression and the file is normal: ${file.isFile}")
                    val compressedImageFile = if (Build.VERSION.SDK_INT >= 30) {
                        Timber.tag("Add Image").i(">30")
                        Compressor.compress(context, file) {
                            size(51200) // 2 MB
                        }
                    }else{
                        Timber.tag("Add Image").i("<30")
                            Compressor.compress(context, file) {
                                size(51200) // 2 MB
                            }
                    }

                    file.delete()

                    localUri.value = compressedImageFile.toUri()
                    croppingInProgress.value = false
                    Timber.tag("Add Image").i("compressed cropped uri: ${localUri.value}")

                }
            }
        } else {
            // an error occurred cropping
//            val exception = result.error
            croppingError.value = true
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions())
        imageCropLauncher.launch(cropOptions)
    }

    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
        CharTrackerTopBar(onBackNav= { confirmBack = true }, actionButtons = {
            IconButton(onClick = {
                if (character.name.value != "" && !croppingInProgress.value) {
                    submitCharacter(character, localUri.value)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(id = R.string.submit_char)
                )
            }
            if (editing){
                IconButton(onClick = { confirmDelete = true }) {
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
                            contentDescription = stringResource(id = R.string.character_image_desc),
                            loading = placeholder(R.drawable.baseline_downloading_24),
                            failure = placeholder(R.drawable.baseline_broken_image_24),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)){
                            it
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        }
                        Button(onClick = {localUri.value = null}) {
                            Text(text = stringResource(id = R.string.remove_selected_image))
                        }
                    }else{
                        Spacer(modifier = Modifier
                            .size(120.dp))
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
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
            FactionsChipGroup(
                header = stringResource(id = R.string.faction),
                contentsList = currentFactions,
                selectedList = character.faction.value,
                onClick = updateFactions)
            ChipGroupRow(
                header = stringResource(id = R.string.allies),
                contentsList = charactersStringList,
                selectedList = character.allies.value,
                onClick = updateAllies
            )
            ChipGroupRow(
                header = stringResource(id = R.string.enemies),
                contentsList = charactersStringList,
                selectedList = character.enemies.value,
                onClick = updateEnemies
            )
            ChipGroupRow(
                header = stringResource(id = R.string.neutral),
                contentsList = charactersStringList,
                selectedList = character.neutral.value,
                onClick = updateNeutrals
            )

        }
        if (confirmBack){
            ConfirmDialog(message = stringResource(id = R.string.confirm_back),
                confirm = {
                    confirmBack = false
                    onBackNav()
                          },
                onDismiss = { confirmBack = false}
            )
        }
        if (confirmDelete){
            ConfirmDialog(message = stringResource(id = R.string.confirm_delete_character),
                confirm = {
                    confirmDelete = false
                    deleteCharacter()
                },
                onDismiss = { confirmDelete = false}
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
        if (croppingError.value){
            MessageDialog(message = stringResource(id = R.string.cropping_error)) {
                croppingError.value = false
            }
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
    val factions = mapOf(
        "Straw Hat Pirates" to 0xFF0000FF,
        "Silver Fox Pirates" to 0xff949494,
        "World Government" to 0xffa4ffa4,)
    CharTrackerTheme {
        Surface {
            AddEditCharacterScreen(
                character = character,
                charactersStringList = charactersList,
                updateAllies = {_, _ ->},
                updateEnemies = {_, _ ->},
                updateNeutrals = {_, _ ->},
                currentFactions = factions,
                updateFactions = { _, _ ->},
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
                resetNavToCharacters = { /**/ },
                startImage = null
            ) {

            }
        }
    }
}