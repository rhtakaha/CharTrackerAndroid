package com.chartracker.ui.story

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.chartracker.database.ImageDBInterface
import com.chartracker.database.StoryDBInterface
import com.chartracker.database.StoryEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.ConfirmDialog
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.story.AddEditStoryViewModel
import com.chartracker.viewmodels.story.AddEditStoryViewModelFactory
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddEditStoryScreen(
    navToStories: () -> Unit,
    onBackNav: () -> Unit,
    storyId: String?,
    storyDB: StoryDBInterface,
    imageDB: ImageDBInterface,
    addEditStoryViewModel: AddEditStoryViewModel = viewModel(factory = AddEditStoryViewModelFactory(storyId, storyDB, imageDB))
){
    AddEditStoryScreen(
        story = addEditStoryViewModel.story.value,
        editing = storyId != null,
        submitStory = {story, localImageURI -> addEditStoryViewModel.submitStory(story, localImageURI)},
        deleteStory = { addEditStoryViewModel.submitStoryDelete() },
        uploadError = addEditStoryViewModel.uploadError.value,
        resetUploadError = { addEditStoryViewModel.resetUploadError() },
        retrievalError = addEditStoryViewModel.retrievalError.value,
        resetRetrievalError = { addEditStoryViewModel.resetRetrievalError() },
        duplicateTitleError = addEditStoryViewModel.duplicateTitleError.value,
        resetDuplicateTitleError = { addEditStoryViewModel.resetDuplicateTitleError() },
        readyToNavToStories = addEditStoryViewModel.navToStories.value,
        navToStories = navToStories,
        resetNavToStories = {addEditStoryViewModel.resetNavToStories()},
        startImage = addEditStoryViewModel.story.value.imagePublicUrl.value?.toUri(),
        onBackNav = onBackNav)
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun AddEditStoryScreen(
    story: StoryEntity,
    editing: Boolean = false,
    submitStory: (StoryEntity, Uri?) -> Unit,
    deleteStory: () -> Unit,
    uploadError: Boolean,
    resetUploadError: () -> Unit,
    retrievalError: Boolean,
    resetRetrievalError: () -> Unit,
    duplicateTitleError: Boolean,
    resetDuplicateTitleError: () -> Unit,
    readyToNavToStories: Boolean,
    navToStories: () -> Unit,
    resetNavToStories: () -> Unit,
    startImage: Uri?,
    onBackNav: () -> Unit
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    if (readyToNavToStories){
        resetNavToStories()
        navToStories()
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

            if (path != null) {
                scope.launch {
                    val file =  File(path)
                    val compressedImageFile = if (Build.VERSION.SDK_INT >= 30) {
                        Compressor.compress(context, file) {
                            size(51200) // 2 MB
                        }
                    }else{
                        Compressor.compress(context, file) {
                            size(51200) // 2 MB
                        }
                    }
                    file.delete()

                    localUri.value = compressedImageFile.toUri()
                    croppingInProgress.value = false
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
        CharTrackerTopBar(onBackNav= {confirmBack = true}, actionButtons = {
            IconButton(onClick = {
                if (story.name.value != "" && !croppingInProgress.value) {
                    submitStory(story, localUri.value)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(id = R.string.submit_story)
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
    }) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
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
                                .clip(CircleShape)){
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
                title = R.string.title,
                label = R.string.title_hint,
                text = story.name.value,
                onTyping = {newInput -> story.name.value = newInput})
            TextEntryHolder(
                title = R.string.author,
                label = R.string.author_hint,
                text = story.author.value,
                onTyping = {newInput -> story.author.value = newInput})
            TextEntryHolder(
                title = R.string.genre,
                label = R.string.genre_hint,
                text = story.genre.value,
                onTyping = {newInput -> story.genre.value = newInput})
            TextEntryHolder(
                title = R.string.type,
                label = R.string.type_hint,
                text = story.type.value,
                onTyping = {newInput -> story.type.value = newInput})

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
            ConfirmDialog(message = stringResource(id = R.string.confirm_delete_story),
                confirm = {
                    confirmDelete = false
                    deleteStory()
                },
                onDismiss = { confirmDelete = false}
            )
        }
        if (uploadError){
            MessageDialog(
                message = stringResource(id = R.string.story_upload_error),
                onDismiss = { resetUploadError() }
            )
        }
        if (retrievalError){
            MessageDialog(
                message = stringResource(id = R.string.retrieval_error),
                onDismiss = { resetRetrievalError() }
            )
        }
        if (duplicateTitleError){
            MessageDialog(
                message = stringResource(id = R.string.duplicate_title_error),
                onDismiss = { resetDuplicateTitleError() }
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
fun PreviewAddStoryScreen(){
    val story by remember {
        mutableStateOf(StoryEntity())
    }
    CharTrackerTheme {
        Surface {
            AddEditStoryScreen(
                story = story,
                submitStory = { _, _ ->},
                deleteStory = {},
                uploadError = false,
                resetUploadError = {},
                retrievalError = false,
                resetRetrievalError = {},
                duplicateTitleError = false,
                resetDuplicateTitleError = {},
                readyToNavToStories = false,
                navToStories = {},
                startImage = null,
                resetNavToStories = { /**/ }) {
            }
        }
    }
}