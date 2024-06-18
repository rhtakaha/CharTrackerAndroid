package com.chartracker.ui.components

import androidx.compose.foundation.layout.Box
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R

@Composable
fun SortingMenu(
    alphaSort: () -> Unit,
    reverseAlphaSort: () -> Unit,
    recentSort: () -> Unit,
    reverseRecentSort: () -> Unit,
    modifier: Modifier = Modifier
    ){
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {

        IconButton(onClick = { expanded = true }) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = stringResource(id = R.string.sorting_menu_desc))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.alphabetical)) },
                onClick = {
                    alphaSort()
                    expanded = false
                          },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.SortByAlpha,
                        contentDescription = stringResource(id = R.string.alphabetical)
                    )
                })
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.reverse_alphabetical)) },
                onClick = {
                    reverseAlphaSort()
                    expanded = false
                          },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.SortByAlpha,
                        contentDescription = stringResource(id = R.string.reverse_alphabetical)
                    )
                })
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.recent)) },
                onClick = {
                    recentSort()
                    expanded = false
                          },
                leadingIcon = {
                    Icon(
                        Icons.Filled.History,
                        contentDescription = stringResource(id = R.string.recent)
                    )
                })
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.reverse_recent)) },
                onClick = {
                    reverseRecentSort()
                    expanded = false
                          },
                leadingIcon = {
                    Icon(
                        Icons.Filled.History,
                        contentDescription = stringResource(id = R.string.reverse_recent)
                    )
                })
        }
    }

}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Email Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Email Light Mode")
@Composable
fun PreviewFilteringMenu(){
    CharTrackerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SortingMenu(
                alphaSort = { /**/ },
                reverseAlphaSort = { /**/ },
                recentSort = { /**/ },
                reverseRecentSort = {})
        }
    }
}
