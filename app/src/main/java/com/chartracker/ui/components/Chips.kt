package com.chartracker.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun ChipGroupRow(
    header: String,
    contentsList: List<String>,
    selectedList: List<String>?,
    onClick: (String, Boolean) -> Unit){
    Column {
        Text(text = header)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
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
    }
}

@SuppressLint("PrivateResource")
@Composable
fun BasicChip(
    isSelected: Boolean = false,
    text: String,
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
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewChipGroupRow(){
    val characters = listOf("Aragorn", "Gandalf", "Merry", "Pippin", "Sam", "Frodo")
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