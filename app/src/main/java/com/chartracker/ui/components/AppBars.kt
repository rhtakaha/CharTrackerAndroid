package com.chartracker.ui.components


import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme

/* Action button ordering is First in First Left order*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharTrackerTopBar(
    title: String? = null,
    onBackNav: ()-> Unit,
    actionButtons: @Composable () -> Unit
){
    TopAppBar(
        title = {
            if (title != null){
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        },
        colors =  TopAppBarDefaults.topAppBarColors(
        containerColor =  MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(
                onClick = { onBackNav() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.up_button)
                )
            }
        },
        actions = {actionButtons()},
        modifier = Modifier.semantics { contentDescription = "Top bar" }
    )
}

@Preview
@Composable
fun PreviewCharTrackerDefaultTopBar(){
    CharTrackerTheme {
        CharTrackerTopBar(
            onBackNav = { /**/ },
            actionButtons = {})
    }
}

@Preview
@Composable
fun PreviewCharTrackerTopBarWithTitle(){
    CharTrackerTheme {
        CharTrackerTopBar(
            title = stringResource(id = R.string.stories),
            onBackNav = { /**/ },
            actionButtons = {})
    }
}

@Preview
@Composable
fun PreviewCharTrackerTopBarWithLongTitleAndTwoActions(){
    CharTrackerTheme {
        CharTrackerTopBar(
            title = "Lord of the Rings",
            onBackNav = { /**/ },
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