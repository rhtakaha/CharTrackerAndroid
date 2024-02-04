package com.chartracker.ui.characters

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.database.CharacterEntity
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun AddEditCharacterScreen(
    onBackNav: () -> Unit,
){

}

@Composable
fun AddEditCharacterScreen(
    name: String,
    onNameChange: (String) -> Unit,
    aliases: String,
    onAliasesChange: (String) -> Unit,
    titles: String,
    onTitlesChange: (String) -> Unit,
    age: String,
    onAgeChange: (Int) -> Unit,
    home: String,
    onHomeChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    race: String,
    onRaceChange: (String) -> Unit,
    livingOrDead: String,
    onLivingOrDeadChange: (String) -> Unit,
    occupation: String,
    onOccupationChange: (String) -> Unit,
    weapons: String,
    onWeaponsChange: (String) -> Unit,
    toolsEquipment: String,
    onToolsEquipmentChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    faction: String,
    onFactionChange: (String) -> Unit,
    allies: List<String>,
    onAlliesChange: (List<String>) -> Unit,
    enemies: List<String>,
    onEnemiesChange: (List<String>) -> Unit,
    neutral: List<String>,
    onNeutralChange: (List<String>) -> Unit,
    startImage: Uri?,
    onBackNav: () -> Unit,
){

}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewAddCharacterScreen(){
    CharTrackerTheme {
        Surface {
            AddEditCharacterScreen(
                onBackNav = {}
            )
        }
    }
}