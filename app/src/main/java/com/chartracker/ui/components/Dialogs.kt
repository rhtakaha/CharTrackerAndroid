package com.chartracker.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme


@Composable
fun MessageDialog(
    @StringRes message: Int,
    onDismiss: () -> Unit
){
    /* Dialog for instances where just need to inform the user about something*/
    AlertDialog(
        onDismissRequest = { },
        text = { Text(text = stringResource(id = message), textAlign = TextAlign.Center) },
        confirmButton = {
            TextButton(
                onClick = {onDismiss()}
            ) {
                Text("Dismiss")
            }
        })
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
                message = R.string.invalid_email_password,
                onDismiss = {}
            )
        }
    }
}