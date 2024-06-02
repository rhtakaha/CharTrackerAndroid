package com.chartracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import timber.log.Timber


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

@Composable
fun ColorPickerDialog(
    onDismiss: () -> Unit,
    color: MutableState<Long>
) {
    val controller = rememberColorPickerController()
    var currColor = color.value
//    controller.setPaletteContentScale(PaletteContentScale.FIT) // scale the image to fit width and height.
//    controller.setPaletteContentScale(PaletteContentScale.CROP) // center crop the image.
//    controller.setWheelRadius(20.dp)
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        // do something
//                    color.value = colorEnvelope.color.value
                        currColor = colorEnvelope.hexCode.toLong(16)
                        Timber.tag("color").i(
                            "${colorEnvelope.color.value} and hex: ${
                                colorEnvelope.hexCode.toLong(16)
                            }"
                        )

                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            color.value = currColor
                            onDismiss()
                                  },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(id = R.string.submit))
                    }
                }


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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewColorPickerDialog(){
    val color = remember { mutableLongStateOf(0) }
    CharTrackerTheme {
        Surface {
            ColorPickerDialog(
                onDismiss = {},
                color = color
            )
        }
    }
}