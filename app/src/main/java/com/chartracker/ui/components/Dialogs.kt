package com.chartracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme


@Composable
fun MessageDialog(
    message: String,
    onDismiss: () -> Unit
){
    /* Dialog for instances where just need to inform the user about something*/
    AlertDialog(
        onDismissRequest = { onDismiss()},
        text = { Text(text = message, textAlign = TextAlign.Center) },
        confirmButton = {
            TextButton(
                onClick = {onDismiss()}
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        })
}


@Composable
fun RefreshDialog(
    message: String,
    refresh: () -> Unit,
    onDismiss: () -> Unit
){
    /* Dialog for instances where couldn't retrieve the information so can refresh to try again*/
    AlertDialog(
        onDismissRequest = { onDismiss()},
        text = { Text(text = message, textAlign = TextAlign.Center) },
        dismissButton = {
            TextButton(
                onClick = {onDismiss()}
            ) {
                Text(stringResource(id = R.string.dismiss))
            }      
        },
        confirmButton = {
            TextButton(
                onClick = {refresh()}
            ) {
                Text(stringResource(id = R.string.refresh))
            }
        })
}

@Composable
fun ConfirmDialog(
    message: String,
    confirm: () -> Unit,
    onDismiss: () -> Unit
){
    /* Dialog for instances where alerting the user to make sure they really want to perform this action*/
    AlertDialog(
        onDismissRequest = { onDismiss()},
        text = { Text(text = message, textAlign = TextAlign.Center) },
        dismissButton = {
            TextButton(
                onClick = {onDismiss()}
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {confirm()},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer)
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        })
}

@Composable
fun ReAuthDialog(
    confirmReauthentication: (String) -> Unit,
    onDismiss: () -> Unit
){
    /* Dialogue for reauthentication*/
    var password by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { },
        text = {
            Column {
                Text(text = stringResource(id = R.string.reauth_message))
                TextEntryHolder(
                    title = R.string.password,
                    label = R.string.passwordHint,
                    text = password,
                    onTyping = { newInput -> password = newInput },
                    isPassword = true
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (password != ""){
                        confirmReauthentication(password)
                    }
                }
            ) {
                Text(stringResource(id = R.string.submit))
            }

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
fun PreviewMessageDialog(){
    CharTrackerTheme {
        Surface {
            MessageDialog(
                message = stringResource(id = R.string.invalid_email_password),
                onDismiss = {}
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
fun PreviewRefreshDialog(){
    CharTrackerTheme {
        Surface {
            RefreshDialog(message = "Failed to retrieve characters.", refresh = { /**/ }) {
                
            }
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
fun PreviewConfirmDialog(){
    CharTrackerTheme {
        Surface {
            ConfirmDialog(
                message = "Are you sure you want to delete your account? This cannot be undone!",
                confirm = { /**/ }) {
                
            }
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
fun PreviewReauthenticationDialog(){
    CharTrackerTheme {
        Surface {
            ReAuthDialog(
                confirmReauthentication = {_ ->},
                onDismiss = {}
            )
        }
    }
}