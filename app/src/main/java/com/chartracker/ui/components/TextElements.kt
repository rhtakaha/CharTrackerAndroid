package com.chartracker.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chartracker.R
import com.chartracker.ui.theme.CharTrackerTheme

@Composable
fun TextAndContentHolder(
    @StringRes title: Int,
    body: String,
    modifier: Modifier= Modifier){
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(8.dp)) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark Mode")
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "Light Mode")
@Composable
fun PreviewTextAndContentHolder(){
    val body = "Frodo Baggins"
    CharTrackerTheme {
        Surface {
            TextAndContentHolder(R.string.name, body)
        }
    }
}

@Composable
fun TextEntryHolder(
    @StringRes title: Int,
    @StringRes label: Int,
    text: String,
    onTyping: (String) -> Unit,
    isEmail: Boolean = false,
    isPassword: Boolean = false
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier
        .padding(8.dp)) {
        Text(text = stringResource(id = title), style = MaterialTheme.typography.labelLarge)
        TextField(
            value = text,
            onValueChange = onTyping,
            label = { Text(stringResource(id = label)) },
            singleLine = isPassword or isEmail,
            textStyle = TextStyle(),
            shape = MaterialTheme.shapes.small,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = if (isPassword) {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else if (isEmail) {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else {
                KeyboardOptions(imeAction = ImeAction.Next)
            },
            trailingIcon = {
                if (isPassword){
                    val image = if (passwordVisible){
                        Icons.Filled.Visibility
                    }else{
                        Icons.Filled.VisibilityOff
                    }

                    val description = if (passwordVisible){
                        stringResource(id = R.string.hide_password)
                    }else{
                        stringResource(id = R.string.show_password)
                    }

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                }
            }
        )
    }
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
            title = R.string.email,
            label = R.string.emailHint,
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
            title = R.string.password,
            label = R.string.passwordHint,
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
            title = R.string.email,
            label = R.string.emailHint,
            text = text,
            onTyping = { newInput -> text = newInput },
            isEmail = true
        )
    }
}

/* composable for entering text and adding it*/
@Composable
fun TextEntryAndAddHolder(
    @StringRes label: Int,
    text: String,
    onTyping: (String) -> Unit,
    onAdd: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = text,
            onValueChange = onTyping,
            label = { Text(stringResource(id = label)) },
            singleLine = true,
            textStyle = TextStyle(),
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .padding(8.dp)
        )
        Button(onClick = { onAdd(text) }) {
            Text(text = stringResource(id = R.string.add))
        }
    }
}

@Composable
fun FactionItem(
    @StringRes label: Int,
    text: String,
    color: Long,
    onTyping: (String) -> Unit,
    onUpdate: (String, Int) -> Unit
) {
    var editing by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = {editing = true}){
            Icon(imageVector  = Icons.Filled.Edit, stringResource(id = R.string.edit))
        }
        if (editing){
            TextField(
                value = text,
                onValueChange = onTyping,
                label = { Text(stringResource(id = label)) },
                singleLine = true,
                textStyle = TextStyle(),
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }else{
            Text(text = text)
        }
        Spacer(modifier = Modifier.width(8.dp))

        //Color block
        Surface(color = Color(color), modifier = Modifier.size(25.dp)) {

        }
        //TODO: add the color selector here by clicking on the color square

        if (editing){
            IconButton(
                onClick = { /*TODOonUpdate(text, color)*/ },
                modifier = Modifier.size(20.dp)
            ){
                Icon(imageVector  = Icons.Filled.Done, stringResource(id = R.string.submit))
            }
            IconButton(
                onClick = {editing = false},
                modifier = Modifier.size(20.dp)
            ){
                Icon(imageVector  = Icons.Filled.Cancel, stringResource(id = R.string.cancel))
            }
        }else{
            Spacer(modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewTextEntryAndAddHolder(){
    var text by remember { mutableStateOf("") }
    CharTrackerTheme {
        TextEntryAndAddHolder(
            label = R.string.faction_hint,
            text = text,
            onTyping = { newInput -> text = newInput },
            onAdd = {}
        )
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "Regular Dark Mode")
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "Regular Light Mode")
@Composable
fun PreviewFactionItem(){
    var text by remember { mutableStateOf("Straw Hat Pirates") }
    CharTrackerTheme {
        Surface {
            FactionItem(
                label = R.string.faction_hint,
                text = text,
                color = 0xFF0000FF,
                onTyping = { newInput -> text = newInput }
            ) { _, _ -> }
        }

    }
}