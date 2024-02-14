package com.chartracker.ui.story

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.R
import com.chartracker.database.StoryEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.EntityHolderList
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.story.StoriesViewModel

@Composable
fun StoriesScreen(
    navToAddStory: () -> Unit,
    navToCharacters: (String) -> Unit,
    navToSettings: () -> Unit,
    onBackNav: () -> Unit,//TODO figure out if actually want to go back
    storiesViewModel: StoriesViewModel = viewModel()
){
    val stories by storiesViewModel.stories.collectAsStateWithLifecycle()
    StoriesScreen(
        stories = stories,
        navToAddStory= navToAddStory,
        navToCharacters= navToCharacters,
        navToSettings = navToSettings,
        onBackNav = onBackNav
    )
}

@Composable
fun StoriesScreen(
    stories: List<StoryEntity>,
    navToAddStory: () -> Unit,
    navToCharacters: (String) -> Unit,
    navToSettings: () -> Unit,
    onBackNav: () -> Unit){
    Scaffold(
        topBar = { 
            CharTrackerTopBar(
                title =  stringResource(R.string.stories),
                onBackNav = onBackNav,
                actionButtons = {
                    IconButton(onClick = { navToSettings() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                })
                 },
        floatingActionButton = {
            FloatingActionButton(onClick = { navToAddStory() }) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_story_desc))
            }
        }
    ) { paddingValue ->
        EntityHolderList(
            entities = stories,
            onClick = navToCharacters,
            modifier = Modifier
                .padding(paddingValue)
                .semantics { contentDescription = "Stories Screen" })
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewStoriesScreen(){
    val stories = listOf(
        StoryEntity(name = "Lord of the Rings"),
        StoryEntity(name = "Ender's Game"),
        StoryEntity(name = "Batman"),
        StoryEntity(name = "Game of Thrones"),
        StoryEntity(name = "The Chronicles of Narnia"),
        StoryEntity(name = "The Cthulhu Mythos"),
        StoryEntity(name = "Really Really Really Really Long Ahh Title Just to see how it looks"))
    CharTrackerTheme {
        Surface {
            StoriesScreen(stories = stories,
                navToAddStory = { /*TODO*/ },
                navToCharacters = {},
                navToSettings = {}) {

            }
        }
    }
}
