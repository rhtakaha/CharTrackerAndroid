package com.chartracker.ui.characters

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import com.chartracker.database.StoryDBInterface
import com.chartracker.database.StoryEntity
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.EntityHolderList
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.characters.CharactersViewModel
import com.chartracker.viewmodels.characters.CharactersViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@Composable
fun CharactersScreen(
    navToAddCharacter: (String, String, String?) -> Unit,
    navToCharacterDetails: (String, String, String) -> Unit,
    navToEditStory: (String) -> Unit,
    navToSettings: () -> Unit,
    navToFactions: (String, String) -> Unit,
    onBackNav: () -> Unit,
    storyTitle: String,
    storyDB: StoryDBInterface,
    characterDB: CharacterDBInterface,
    charactersViewModel: CharactersViewModel = viewModel(
        factory = CharactersViewModelFactory(
            storyTitle = storyTitle,
            storyDB = storyDB,
            characterDB = characterDB
        )
    )
){
    val characters by charactersViewModel.characters.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    CharactersScreen(
        characters = characters,
        alphaSort = { charactersViewModel.charactersAlphaSort() },
        reverseAlphaSort = { charactersViewModel.charactersReverseAlphaSort() },
        recentSort = { charactersViewModel.charactersRecentSort() },
        reverseRecentSort = { charactersViewModel.charactersReverseRecentSort() },
        failedGetCharacters = charactersViewModel.failedGetCharacters.value,
        resetFailedGetCharacters = { charactersViewModel.resetFailedGetCharacters() },
        refreshCharacters = {
            scope.launch {
                charactersViewModel.getCharacters()
            }
        },
        story = charactersViewModel.story.value,
        navToFactions = { navToFactions(charactersViewModel.storyId, storyTitle) },
        navToAddCharacter = {navToAddCharacter(charactersViewModel.storyId, storyTitle, null)},
        /* convert 2 input lambda into 1 input by adding the storyId here*/
        navToCharacterDetails = {charName -> navToCharacterDetails(charactersViewModel.storyId, storyTitle, charName)},
        navToEditStory = { navToEditStory(charactersViewModel.storyId) },
        navToSettings = navToSettings,
        onBackNav = onBackNav)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun CharactersScreen(
    characters: List<CharacterEntity>,
    alphaSort: () -> Unit,
    reverseAlphaSort: () -> Unit,
    recentSort: () -> Unit,
    reverseRecentSort: () -> Unit,
    failedGetCharacters: Boolean,
    resetFailedGetCharacters: () -> Unit,
    refreshCharacters: () -> Unit,
    story: StoryEntity,
    navToFactions: () -> Unit,
    navToAddCharacter: () -> Unit,
    navToCharacterDetails: (String) -> Unit,
    navToEditStory: () -> Unit,
    navToSettings: () -> Unit,
    onBackNav: () -> Unit,
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing, { refreshCharacters() })
    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CharTrackerTopBar(
                title =  story.name.value,
                onBackNav = onBackNav,
                actionButtons = {
                    IconButton(onClick = { navToEditStory() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(id =R.string.edit_story)
                        )
                    }
                    IconButton(onClick = { navToSettings() }) {
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
        Box(modifier = Modifier
            .padding(paddingValue)
            .pullRefresh(pullRefreshState)
        ){
            EntityHolderList(
                entities = characters,
                story = story,
                onClick = navToCharacterDetails,
                navToFactions = navToFactions,
                alphaSort = alphaSort,
                reverseAlphaSort = reverseAlphaSort,
                recentSort = recentSort,
                reverseRecentSort = reverseRecentSort,
                modifier = Modifier
                    .semantics { contentDescription = "Characters Screen" }
            )

            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
        if (failedGetCharacters){
            MessageDialog(
                message = stringResource(id = R.string.failed_get_characters),
                onDismiss = { resetFailedGetCharacters() }
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
fun PreviewCharactersScreen(){
    val characters = listOf(
        CharacterEntity(name = "Frodo Baggins")
    )
    val story = StoryEntity(name = "Lord of the Rings",
        author = "JRR Tolkien",
        genre = "Epic Fantasy",
        type = "Book/film")
    CharTrackerTheme {
        Surface {
            CharactersScreen(
                characters = characters,
                alphaSort = {},
                reverseAlphaSort = {},
                recentSort = {},
                reverseRecentSort = {},
                failedGetCharacters = false,
                resetFailedGetCharacters = {},
                refreshCharacters = {},
                story = story,
                navToFactions = {},
                navToAddCharacter = {},
                navToCharacterDetails = {},
                navToEditStory = {},
                navToSettings = {},
                onBackNav = { /**/ })
        }
    }
}