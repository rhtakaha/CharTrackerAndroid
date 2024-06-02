package com.chartracker.ui.story

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.database.CharacterDBInterface
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.FactionItemsList
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryAndAddHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.story.FactionsViewModel
import com.chartracker.viewmodels.story.FactionsViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@Composable
fun FactionsScreen(
    onBackNav: () -> Unit,
    storyTitle: String,
    storyId: String,
    characterDB: CharacterDBInterface,
    factionsViewModel: FactionsViewModel = viewModel(
        factory = FactionsViewModelFactory(
            storyId = storyId,
            characterDB = characterDB
        )
    )
){
    val scope = rememberCoroutineScope()
    FactionsScreen(
        factions = factionsViewModel.factions,
        failedGetFactions = factionsViewModel.failedGetFactions.value,
        resetFailedGetFactions = { factionsViewModel.resetFailedGetFactions() },
        refreshFactions = {
            scope.launch {
                factionsViewModel.getFactions()
            }
        },
        onUpdate = { originalName: String, currentName: String, color: Long -> factionsViewModel.updateFaction(originalName, currentName, color)},
        storyTitle = storyTitle,
        onBackNav = onBackNav)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun FactionsScreen(
    factions: Map<String, Long>,
    failedGetFactions: Boolean,
    resetFailedGetFactions: () -> Unit,
    refreshFactions: () -> Unit,
    onUpdate: (String, String, Long) -> Unit,
    storyTitle: String,
    onBackNav: () -> Unit,
){
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing, { refreshFactions() })
    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CharTrackerTopBar(
                title =  storyTitle,
                onBackNav = onBackNav,
                actionButtons = {
                })
        }
    ) {paddingValue ->
        Box(modifier = Modifier
            .padding(paddingValue)
            .pullRefresh(pullRefreshState)
        ){
            Column {
                TextEntryAndAddHolder(
                    label = R.string.faction_hint,
                    text = text,
                    onTyping = { newInput -> text = newInput },
                    onAdd = {/*TODO*/}
                )
                FactionItemsList(
                    factions = factions,
                    onUpdate = onUpdate
                )
            }

            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
        if (failedGetFactions){
            MessageDialog(
                message = stringResource(id = R.string.failed_get_factions),
                onDismiss = { resetFailedGetFactions() }
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
fun PreviewFactionsScreen(){
    val factions = hashMapOf(
        "Straw Hat Pirates" to 0xFF0000FF,
        "Silver Fox Pirates" to 0xff949494,
        "World Government" to 0xffa4ffa4,
    )
    CharTrackerTheme {
        Surface {
            FactionsScreen(
                factions = factions,
                failedGetFactions = false,
                resetFailedGetFactions = {},
                refreshFactions = {},
                onUpdate = { _, _, _ ->},
                storyTitle = "Lord of the Rings",
                onBackNav = { /**/ })
        }
    }
}