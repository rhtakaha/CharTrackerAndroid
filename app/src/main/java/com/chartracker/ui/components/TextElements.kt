package com.chartracker.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.chartracker.ui.theme.shapes
import kotlinx.coroutines.launch

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
fun FactionItemsList(
    editing: Boolean,
    factions: Map<String, Long>,
    modifier: Modifier=Modifier,
    onUpdate: (String, String, Long) -> Unit,
    onDelete: (String) -> Unit
){
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember{
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }
    Surface {
        Box(modifier= modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.End) {
                LazyColumn(
                    contentPadding = PaddingValues(4.dp),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    factions.forEach { (name, color) ->
                        item {
                            FactionItem(
                                editing = editing,
                                originalName = name,
                                initColor = color,
                                onUpdate = onUpdate,
                                onDelete = onDelete
                            )
                        }
                    }
                }

            }
            if (showScrollToTopButton) {
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = stringResource(id = R.string.scroll_to_top)
                    )
                }
            }
        }
    }
}

@Composable
fun FactionItem(
    editing: Boolean,
    originalName: String,
    initColor: Long? = null,
    onUpdate: (String, String, Long) -> Unit,
    onDelete: (String) -> Unit
) {
    var pickingColor by rememberSaveable { mutableStateOf(false) }
    val currName = remember { mutableStateOf(originalName) }

    val color = if (initColor != null){
        remember { mutableLongStateOf(initColor) }
    }else{
        remember { mutableLongStateOf(0xFF0000FF) }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (editing){
            TextField(
                value = currName.value,
                onValueChange = { newInput -> currName.value = newInput },
                label = { Text(stringResource(id = R.string.faction_hint)) },
                singleLine = true,
                textStyle = TextStyle(),
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .padding(2.dp)
                    .weight(6f, fill = false)
            )
        }else{
            Text(text = currName.value)
        }

        if (!pickingColor){
            //Color block
            Button(
                onClick = { if (editing) {pickingColor = true} },
                colors = ButtonColors(
                    containerColor = Color(color.longValue),
                    contentColor = ButtonDefaults.buttonColors().contentColor,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                    disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                ),
                shape = shapes.extraSmall,
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f, fill = false)
            ) {

            }
        }else{
            ColorPickerDialog(
                onDismiss = { pickingColor = false},
                color = color
            )
        }

        if (editing){
            IconButton(
                onClick = { onUpdate(originalName, currName.value, color.longValue) },
                colors = IconButtonColors(
                    containerColor = IconButtonDefaults.filledIconButtonColors().containerColor,
                    contentColor = IconButtonDefaults.filledIconButtonColors().contentColor,
                    disabledContainerColor = IconButtonDefaults.filledIconButtonColors().disabledContainerColor,
                    disabledContentColor = IconButtonDefaults.filledIconButtonColors().disabledContentColor
                ),
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f, fill = false)
            ){
                Icon(imageVector  = Icons.Filled.Done, stringResource(id = R.string.submit))
            }
            IconButton(
                onClick = { onDelete(originalName) },
                colors = IconButtonColors(
                    containerColor = IconButtonDefaults.filledIconButtonColors().containerColor,
                    contentColor = IconButtonDefaults.filledIconButtonColors().contentColor,
                    disabledContainerColor = IconButtonDefaults.filledIconButtonColors().disabledContainerColor,
                    disabledContentColor = IconButtonDefaults.filledIconButtonColors().disabledContentColor
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.delete_faction)
                )
            }
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
    val text by remember { mutableStateOf("Straw Hat Pirates") }
    CharTrackerTheme {
        Surface {
            FactionItem(
                editing = false,
                originalName = text,
                onUpdate = { _, _, _ -> },
                onDelete = {}
            )
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
fun PreviewFactionItemsList(){
    val factions = hashMapOf(
        "Straw Hat Pirates" to 0xFF0000FF,
        "Silver Fox Pirates" to 0xff949494,
        "World Government" to 0xffa4ffa4,
    )
    CharTrackerTheme {
        Surface {
            FactionItemsList(
                editing = false,
                factions = factions,
                onUpdate = {_, _, _ ->},
                onDelete = {}
                )
        }
    }
}