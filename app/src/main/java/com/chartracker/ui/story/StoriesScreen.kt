package com.chartracker.ui.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.database.StoriesEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.EntityHolderList

@Composable
fun Test(refreshStories: () -> Unit){
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(key1 = lifecycle){
        // should make it so stories get refreshed whenever we come back here
        val lifecycleObserver = getStoriesObserver { refreshStories() }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }

    }
}
@Composable
fun StoriesScreen(
    navToAddStory: () -> Unit,
    navToEditStory: (String?) -> Unit,
    onBackNav: () -> Unit,//TODO figure out if actually want to go back
    storiesViewModel: StoriesViewModel = viewModel()
){
    val stories by storiesViewModel.stories.collectAsStateWithLifecycle()
//    Test { storiesViewModel.getStories() }
    StoriesScreen(
        stories = stories,
        navToAddStory= navToAddStory,
        navToEditStory= navToEditStory,
        onBackNav = onBackNav
    )
}

//TODO will need FAB for adding story
@Composable
fun StoriesScreen(
    stories: List<StoriesEntity>,
    navToAddStory: () -> Unit,
    navToEditStory: (String?) -> Unit,
    onBackNav: () -> Unit){
    Scaffold(topBar = { CharTrackerTopBar(onBackNav) {} }) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .semantics { contentDescription = "Stories Screen" }){
            Button(onClick = {navToAddStory()}) {
                Text(text = "Add story")
            }
            EntityHolderList(stories = stories)
        }
    }
//        val db = DatabaseAccess()
//        EntityHolder(imageUri = db.getImageRef("character_Lord of the Rings_Sauron.png"), entityName = "Ring")
}
private fun getStoriesObserver(refreshStories: () -> Unit): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_CREATE ) {
            refreshStories()
        }
    }
