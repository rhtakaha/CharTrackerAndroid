package com.chartracker.ui.characters

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import com.chartracker.database.CharacterEntity
import com.chartracker.database.StoryEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.EntityHolderList
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.characters.CharactersViewModel
import com.chartracker.viewmodels.characters.CharactersViewModelFactory

@Composable
fun CharactersScreen(
    navToAddCharacter: () -> Unit,
    navToCharacterDetails: (String, String) -> Unit,
    onBackNav: () -> Unit,
    storyTitle: String,
    charactersViewModel: CharactersViewModel = viewModel(factory = CharactersViewModelFactory(storyTitle = storyTitle))
){
    val characters by charactersViewModel.characters.collectAsStateWithLifecycle()
    val story by charactersViewModel.story.collectAsStateWithLifecycle()
    val charactersStringList by charactersViewModel.charactersStringList.collectAsStateWithLifecycle()
    CharactersScreen(
        characters = characters,
        story = story,
        charactersStringList= charactersStringList,
        navToAddCharacter = navToAddCharacter,
        /* convert 2 input lambda into 1 input by adding the storyId here*/
        navToCharacterDetails = {charName -> navToCharacterDetails(charactersViewModel.storyId, charName)},
        onBackNav = onBackNav)
}

@Composable
fun CharactersScreen(
    characters: List<CharacterEntity>,
    story: StoryEntity,
    charactersStringList: MutableList<String>,
    navToAddCharacter: () -> Unit,// might need params
    navToCharacterDetails: (String) -> Unit,
    onBackNav: () -> Unit,
){
    Scaffold(
        topBar = {
            CharTrackerTopBar(
                title =  story.name,
                onBackNav = onBackNav,
                actionButtons = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(id =R.string.edit_story)
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navToAddCharacter() }) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_character_desc))
            }
        }
    ) {paddingValue ->
        EntityHolderList(
            entities = characters,
            story = story,
            onClick = navToCharacterDetails,
            modifier = Modifier
                .padding(paddingValue)
                .semantics { contentDescription = "Characters Screen" }
            )

    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewCharactersScreen(){
    val characters = listOf(
        CharacterEntity(name = "Frodo Baggins")
    )
    val charactersStringList = mutableListOf("Frodo Baggins, Gandalf, Aragorn")
    val story = StoryEntity(name = "Lord of the Rings",
        author = "JRR Tolkien",
        genre = "Epic Fantasy",
        type = "Book/film")
    CharTrackerTheme {
        Surface {
            CharactersScreen(
                characters = characters,
                story = story,
                charactersStringList= charactersStringList,
                navToAddCharacter = {},
                navToCharacterDetails = {},
                onBackNav = { /*TODO*/ })
        }
    }
}