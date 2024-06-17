package com.chartracker.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FactionsChipGroup(
    header: String,
    contentsList: Map<String, Long>,
    selectedList: List<String>?,
    onClick: (String, Boolean) -> Unit){
    Column {
        Text(text = header)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_pad))
        )  {
            for (item in contentsList){
                if (selectedList != null) {
                    // if there are some selected check if this is one of them
                    BasicChip(
                        isSelected = item.key in selectedList,
                        text = item.key,
                        color = item.value,
                        onClick = {selected -> onClick(item.key, selected)}
                    )
                }else{
                    BasicChip(
                        text = item.key,
                        color = item.value,
                        onClick = {selected -> onClick(item.key, selected)}
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroupRow(
    header: String,
    contentsList: List<String>,
    selectedList: List<String>?,
    onClick: (String, Boolean) -> Unit){
    Column {
        Text(text = header)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_pad))
        )  {
            for (item in contentsList){
                if (selectedList != null) {
                    // if there are some selected check if this is one of them
                    BasicChip(
                        isSelected = item in selectedList,
                        text = item,
                        onClick = {selected -> onClick(item, selected)}
                    )
                }else{
                    BasicChip(
                        text = item,
                        onClick = {selected -> onClick(item, selected)}
                    )
                }
            }
        }

//        Row(
//            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_pad)),
//            modifier = Modifier
//                .horizontalScroll(rememberScrollState())
//        ) {
//            for (item in contentsList){
//                if (selectedList != null) {
//                    // if there are some selected check if this is one of them
//                    BasicChip(
//                        isSelected = item in selectedList,
//                        text = item,
//                        onClick = {selected -> onClick(item, selected)}
//                    )
//                }else{
//                    BasicChip(
//                        text = item,
//                        onClick = {selected -> onClick(item, selected)}
//                    )
//                }
//            }
//        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("PrivateResource")
@Composable
fun BasicChip(
    isSelected: Boolean = false,
    text: String,
    color: Long?=null,
    onClick: (Boolean) -> Unit) {
    var selected by remember { mutableStateOf(isSelected) }

    FilterChip(
        onClick = {
            selected = !selected
            onClick(selected)
                  },
        label = {
            Text(text)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(id = R.string.selected_chip),
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        border = if (color != null) {
            BorderStroke(width = 1.dp, Color(color) )
        }else{
            BorderStroke(width = 1.dp, Color.Black )
        }

    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewFactionsChipGroup(){
    val factions = mapOf(
        "Straw Hat Pirates" to 0xFF0000FF,
        "Silver Fox Pirates" to 0xff949494,
        "World Government" to 0xffa4ffa4,)
    CharTrackerTheme {
        Surface {
            FactionsChipGroup(
                header = "Factions:",
                contentsList = factions,
                selectedList = listOf(),
                onClick = {_, _ ->})
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewChipGroupRow(){
    val characters = listOf("Aragorn", "Gandalf", "Merry", "Pippin", "Sam", "Frodo", "Sauron", "Tom Bombadil", "Borimir", "Farimir")
    CharTrackerTheme {
        Surface {
            ChipGroupRow(
                header = "Allies:",
                contentsList = characters,
                selectedList = listOf(),
                onClick = {_, _ ->}
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewChip(){
    CharTrackerTheme {
        Surface {
            BasicChip(
                text ="character",
                onClick = {}
                )
        }
    }
}