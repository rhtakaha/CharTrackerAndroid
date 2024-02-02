package com.chartracker.ui.story

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.R
import com.chartracker.database.StoriesEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.EntityHolderList
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun StoriesScreen(
    navToAddStory: () -> Unit,
    onBackNav: () -> Unit,//TODO figure out if actually want to go back
    storiesViewModel: StoriesViewModel = viewModel()
){
    val stories by storiesViewModel.stories.collectAsStateWithLifecycle()
    StoriesScreen(
        stories = stories,
        navToAddStory= navToAddStory,
        onBackNav = onBackNav
    )
}

//TODO will need FAB for adding story
@Composable
fun StoriesScreen(
    stories: List<StoriesEntity>,
    navToAddStory: () -> Unit,
    onBackNav: () -> Unit){
    Scaffold(
        topBar = { 
            CharTrackerTopBar(
                title =  R.string.stories,
                onBackNav = onBackNav,
                actionButtons = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                })
                 },
        floatingActionButton = {
            FloatingActionButton(onClick = { navToAddStory() }) {
                Icon(Icons.Filled.Add, "Add a story.")
            }
        }
    ) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .semantics { contentDescription = "Stories Screen" }){
//            Button(onClick = {navToAddStory()}) {
//                Text(text = "Add story")
//            }
            EntityHolderList(entities = stories)
        }
    }
//        val db = DatabaseAccess()
//        EntityHolder(imageUri = db.getImageRef("character_Lord of the Rings_Sauron.png"), entityName = "Ring")
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
        StoriesEntity(name = "Lord of the Rings"),
        StoriesEntity(name = "Ender's Game"),
        StoriesEntity(name = "Batman"),
        StoriesEntity(name = "Game of Thrones"),
        StoriesEntity(name = "The Chronicles of Narnia"),
        StoriesEntity(name = "The Cthulhu Mythos"),
        StoriesEntity(name = "Really Really Really Really Long Ahh Title Just to see how it looks"))
    CharTrackerTheme {
        Surface {
            StoriesScreen(stories = stories,
                navToAddStory = { /*TODO*/ }) {

            }
        }
    }
}
