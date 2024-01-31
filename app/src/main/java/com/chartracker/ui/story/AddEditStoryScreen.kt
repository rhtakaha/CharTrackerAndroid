package com.chartracker.ui.story

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.chartracker.R
import com.chartracker.database.StoriesEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.TextEntryHolder

@Composable
fun AddEditStoryScreen(
    onBackNav: () -> Unit,
    addEditStoryViewModel: AddEditStoryViewModel = viewModel()
){
    AddEditStoryScreen(
        title = addEditStoryViewModel.title.value,
        onTitleChange= {newInput: String -> addEditStoryViewModel.updateInputTitle(newInput)},
        author = addEditStoryViewModel.author.value,
        onAuthorChange = {newInput: String -> addEditStoryViewModel.updateInputAuthor(newInput)},
        genre = addEditStoryViewModel.genre.value,
        onGenreChange = {newInput: String -> addEditStoryViewModel.updateInputGenre(newInput)},
        type = addEditStoryViewModel.type.value,
        onTypeChange = {newInput: String -> addEditStoryViewModel.updateInputType(newInput)},
        submitStory = {story, localImageURI -> addEditStoryViewModel.submitStory(story, localImageURI)},
        navToStories = addEditStoryViewModel.navToStories.value,
        resetNavToStories = {addEditStoryViewModel.resetNavToStories()},
        onBackNav = onBackNav)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddEditStoryScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    author: String,
    onAuthorChange: (String) -> Unit,
    genre: String,
    onGenreChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    submitStory: (StoriesEntity, Uri?) -> Unit,
    navToStories: Boolean,
    resetNavToStories: () -> Unit,
    onBackNav: () -> Unit
){
    if (navToStories){
        onBackNav()
        resetNavToStories()
    }
    val localUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia() ){
        localUri.value = it
    }
    Scaffold(topBar = { CharTrackerTopBar(onBackNav) {} }) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .semantics { contentDescription = "AddEditStory Screen" }){
            Button(onClick = {
                val story = StoriesEntity(title, genre, type, author)
                submitStory(story, localUri.value) }) {
                Text(text = stringResource(id = R.string.submit))
            }
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
                title = R.string.title,
                label = R.string.title_hint,
                text = title,
                onTyping = {newInput -> onTitleChange(newInput)})
            TextEntryHolder(
                title = R.string.author,
                label = R.string.author_hint,
                text = author,
                onTyping = {newInput -> onAuthorChange(newInput)})
            TextEntryHolder(
                title = R.string.genre,
                label = R.string.genre_hint,
                text = genre,
                onTyping = {newInput -> onGenreChange(newInput)})
            TextEntryHolder(
                title = R.string.type,
                label = R.string.type_hint,
                text = type,
                onTyping = {newInput -> onTypeChange(newInput)})


        }
    }

}