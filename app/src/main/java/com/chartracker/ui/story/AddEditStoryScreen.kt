package com.chartracker.ui.story

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.chartracker.R
import com.chartracker.database.StoryEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.story.AddEditStoryViewModel
import com.chartracker.viewmodels.story.AddEditStoryViewModelFactory

@Composable
fun AddEditStoryScreen(
    onBackNav: () -> Unit,
    storyId: String?,
    addEditStoryViewModel: AddEditStoryViewModel = viewModel(factory = AddEditStoryViewModelFactory(storyId))
){
    AddEditStoryScreen(
        story = addEditStoryViewModel.story.value,
        submitStory = {story, localImageURI -> addEditStoryViewModel.submitStory(story, localImageURI)},
        readyToNavToCharacters = addEditStoryViewModel.navToStories.value,
        resetNavToStories = {addEditStoryViewModel.resetNavToStories()},
        startImage = addEditStoryViewModel.story.value.imagePublicUrl.value?.toUri(),
        onBackNav = onBackNav)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddEditStoryScreen(
    story: StoryEntity,
    submitStory: (StoryEntity, Uri?) -> Unit,
    readyToNavToCharacters: Boolean,
    resetNavToStories: () -> Unit,
    startImage: Uri?,
    onBackNav: () -> Unit
){
    if (readyToNavToCharacters){
        resetNavToStories()
        onBackNav()
    }
    val localUri = remember(key1 = startImage) {
        mutableStateOf(startImage)
    }
//    val localUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia() ){
        localUri.value = it
    }
    Scaffold(topBar = { CharTrackerTopBar(onBackNav=onBackNav) {} }) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(paddingValue)
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
            Button(onClick = {
                if (story.name.value != "") {
//                    val story = StoryEntity(title, genre, type, author)
                    submitStory(story, localUri.value)
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
fun PreviewAddStoryScreen(){
    val story by remember {
        mutableStateOf(StoryEntity())
    }
    CharTrackerTheme {
        Surface {
            AddEditStoryScreen(
                story = story,
                submitStory = { _, _ ->},
                readyToNavToCharacters = false,
                startImage = null,
                resetNavToStories = { /*TODO*/ }) {
                
            }
        }
    }
}