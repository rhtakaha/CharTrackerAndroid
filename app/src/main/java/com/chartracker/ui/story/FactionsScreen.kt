package com.chartracker.ui.story

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.database.CharacterDBInterface
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.ConfirmDialog
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
    val factions by factionsViewModel.factions.collectAsStateWithLifecycle()
    FactionsScreen(
        factions = factions,
        failedGetFactions = factionsViewModel.failedGetFactions.value,
        resetFailedGetFactions = { factionsViewModel.resetFailedGetFactions() },
        duplicateNameError = factionsViewModel.duplicateNameError.value,
        resetDuplicateNameError = { factionsViewModel.resetDuplicateNameError() },
        failedUpdateFactions = factionsViewModel.failedUpdateFactions.value,
        resetFailedUpdateFactions = { factionsViewModel.resetFailedUpdateFactions() },
        refreshFactions = {
            scope.launch {
                factionsViewModel.getFactions()
            }
        },
        onCreate = { name: String -> factionsViewModel.createFaction(name) },
        onUpdate = { originalName: String, currentName: String, color: Long -> factionsViewModel.updateFaction(originalName, currentName, color)},
        onDelete = { originalName: String -> factionsViewModel.deleteFaction(originalName) },
        submitFactions = { factionsViewModel.submitFactions() },
        storyTitle = storyTitle,
        onBackNav = onBackNav)
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun FactionsScreen(
    factions: Map<String, Long>,
    failedGetFactions: Boolean,
    resetFailedGetFactions: () -> Unit,
    duplicateNameError: Boolean,
    resetDuplicateNameError: () -> Unit,
    failedUpdateFactions: Boolean,
    resetFailedUpdateFactions: () -> Unit,
    refreshFactions: () -> Unit,
    onCreate: (String) -> Unit,
    onUpdate: (String, String, Long) -> Unit,
    onDelete: (String) -> Unit,
    submitFactions: () -> Unit,
    storyTitle: String,
    onBackNav: () -> Unit,
){
    var editing by rememberSaveable { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing, { refreshFactions() })


    var confirmBack by remember {
        mutableStateOf(false)
    }

    var confirmSubmit by remember {
        mutableStateOf(false)
    }

    BackHandler {
        confirmBack = true
    }

    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CharTrackerTopBar(
                title =  storyTitle,
                onBackNav = { confirmBack = true },
                actionButtons = {
                    if (!editing){
                        IconButton(onClick = { editing = true}) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(id = R.string.edit_factions)
                            )
                        }
                    }else{
                        IconButton(onClick = { confirmSubmit = true }) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(id = R.string.submit_factions)
                            )
                        }
                    }

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
                    onAdd = {s ->
                        onCreate(s)
                        text = ""
                    }
                )
                FactionItemsList(
                    editing = editing,
                    factions = factions,
                    onUpdate = onUpdate,
                    onDelete = onDelete
                )
            }

            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
        if (failedUpdateFactions){
            MessageDialog(
                message = stringResource(id = R.string.failed_update_factions),
                onDismiss = { resetFailedUpdateFactions() }
            )
        }
        if (duplicateNameError){
            MessageDialog(
                message = stringResource(id = R.string.duplicate_faction_error),
                onDismiss = { resetDuplicateNameError() }
            )
        }
        if (failedGetFactions){
            MessageDialog(
                message = stringResource(id = R.string.failed_get_factions),
                onDismiss = { resetFailedGetFactions() }
            )
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
        if (confirmSubmit){
            ConfirmDialog(message = stringResource(id = R.string.confirm_submit_factions),
                confirm = {
                    confirmSubmit = false
                    submitFactions()
                    editing = false
                },
                onDismiss = { confirmSubmit = false}
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
                duplicateNameError = false,
                resetDuplicateNameError = {},
                failedUpdateFactions = false,
                resetFailedUpdateFactions = {},
                resetFailedGetFactions = {},
                refreshFactions = {},
                onCreate = {},
                onUpdate = { _, _, _ ->},
                onDelete = {},
                submitFactions = {},
                storyTitle = "Lord of the Rings",
                onBackNav = { /**/ })
        }
    }
}