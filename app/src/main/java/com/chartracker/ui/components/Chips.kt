package com.chartracker.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun ChipGroupRow(
    header: String,
    contentsList: List<String>,
    onClick: (String, Boolean) -> Unit){
    Column {
        Text(text = header)
        Row {
            for (item in contentsList){
                BasicChip(
                    text = item,
                    onClick = {selected -> onClick(item, selected)}
                )
            }
        }
    }
}

@SuppressLint("PrivateResource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicChip(
    text: String,
    onClick: (Boolean) -> Unit) {
    var selected by remember { mutableStateOf(false) }

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
                    contentDescription = stringResource(id = androidx.compose.ui.R.string.selected),
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
    val characters = listOf("Aragorn", "Gandalf", "Merry", "Pippin")
    CharTrackerTheme {
        Surface {
            ChipGroupRow(
                header = "Allies:",
                contentsList = characters,
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