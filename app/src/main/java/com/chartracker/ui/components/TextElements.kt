package com.chartracker.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun TextEntryHolder(
    label: String,
    text: String,
    onTyping: (String) -> Unit,
    modifier: Modifier= Modifier,
    isEmail: Boolean = false,
    isPassword: Boolean = false
) {
    TextField(
        value = text,
        onValueChange = onTyping,
        label = { Text(label) },
        singleLine = isPassword or isEmail,
        textStyle = TextStyle(),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else if (isEmail) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions()
        },
        modifier = Modifier
            .padding(8.dp)
    )
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewTextEntryHolder(){
    var text by remember { mutableStateOf("") }
    CharTrackerTheme {
        TextEntryHolder(
            label = "Testing",
            text = text,
            onTyping = { newInput -> text = newInput })
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "Password Dark Mode")
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "Password Light Mode")
@Composable
fun PreviewTextEntryHolderPassword(){
    var password by remember { mutableStateOf("") }
    CharTrackerTheme {
        TextEntryHolder(
            label = stringResource(id = R.string.passwordHint),
            text = password,
            onTyping = { newInput -> password = newInput },
            isPassword = true
        )
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "Email Dark Mode")
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "Email Light Mode")
@Composable
fun PreviewTextEntryHolderEmail(){
    var text by remember { mutableStateOf("") }
    CharTrackerTheme {
        TextEntryHolder(
            label = stringResource(id = R.string.emailHint),
            text = text,
            onTyping = { newInput -> text = newInput },
            isEmail = true
        )
    }
}