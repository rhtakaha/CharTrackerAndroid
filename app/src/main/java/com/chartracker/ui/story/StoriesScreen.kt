package com.chartracker.ui.story

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.database.StoriesEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.EntityHolderList

@Composable
fun StoriesScreen(
    onAddEditStoryNav: () -> Unit,
    onBackNav: () -> Unit,//TODO figure out if actually want to go back
    storiesViewModel: StoriesViewModel = viewModel()
){
    val stories by storiesViewModel.stories.collectAsStateWithLifecycle()
    StoriesScreen(
        stories = stories,
        onAddEditStoryNav= onAddEditStoryNav,
        onBackNav = onBackNav
    )
}

//TODO will need FAB for adding story
@Composable
fun StoriesScreen(
    stories: List<StoriesEntity>,
    onAddEditStoryNav: () -> Unit,
    onBackNav: () -> Unit){
    Scaffold(topBar = { CharTrackerTopBar(onBackNav) {} }) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .semantics { contentDescription = "Stories Screen" }){
            Button(onClick = {onAddEditStoryNav()}) {
                Text(text = "Add story")
            }
            EntityHolderList(stories = stories)
        }
    }
//        val db = DatabaseAccess()
//        EntityHolder(imageUri = db.getImageRef("character_Lord of the Rings_Sauron.png"), entityName = "Ring")
}