package com.chartracker.ui.components


import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.ui.theme.CharTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharTrackerTopBar(
    onBackNav: ()-> Unit,
    actionButtons: @Composable () -> Unit //First in First Left
){
    TopAppBar(
        colors =  TopAppBarDefaults.topAppBarColors(
        containerColor =  MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),
        navigationIcon = {
            IconButton(onClick = { onBackNav() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {actionButtons()},
        title = {Text(text = "")}
    )
}

@Preview
@Composable
fun PreviewCharTrackerDefaultTopBar(){
    CharTrackerTheme {
        CharTrackerTopBar(
            onBackNav = { /*TODO*/ },
            actionButtons = {})
    }
}

@Preview
@Composable
fun PreviewCharTrackerTopBarWithTwoActions() {
    CharTrackerTheme {
        CharTrackerTopBar(
            onBackNav = { /*TODO*/ },
            actionButtons = {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = "Localized description"
                    )
                }

            })
    }
}