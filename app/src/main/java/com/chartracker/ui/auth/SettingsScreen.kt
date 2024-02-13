package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.R
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.auth.SettingsViewModel

@Composable
fun SettingsScreen(
//    navToEmailVerify: () -> Unit,
    navToSignIn: () -> Unit,
    onBackNav: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
){
   SettingsScreen(
       updatedEmail = settingsViewModel.updatedEmail.value,
       onUpdatedEmailChange = {newInput -> settingsViewModel.updateUpdatedEmail(newInput)},
       submitUpdatedEmail = {newEmail -> settingsViewModel.updateUserEmail(newEmail)},
//       readyToNavToEmailVerify = settingsViewModel.readyToNavToEmailVerify.value,
//       resetReadyToNavToEmailVerify = { settingsViewModel.resetReadyToNavToEmailVerify() },
//       navToEmailVerify = navToEmailVerify,
       updatedPassword = settingsViewModel.updatedPassword.value,
       onUpdatedPasswordChange = { newInput -> settingsViewModel.updateUpdatedPassword(newInput)},
       submitUpdatedPassword = {newPassword -> settingsViewModel.updatePassword(newPassword)},
       confirmedPassword = settingsViewModel.confirmedPassword.value,
       onConfirmedPasswordChange = { newInput -> settingsViewModel.updateConfirmedPassword(newInput)},
       signOut = { settingsViewModel.signOut() },
       readyToNavToSignIn = settingsViewModel.readyToNavToSignIn.value,
       resetReadyToNavToSignIn = { settingsViewModel.resetReadyToNavToSignIn() },
       navToSignIn = navToSignIn,
       deleteAccount = { settingsViewModel.deleteUser() },
       onBackNav = onBackNav)
}

@Composable
fun SettingsScreen(
    updatedEmail: String,
    onUpdatedEmailChange: (String) -> Unit,
    submitUpdatedEmail: (String) -> Unit,
//    readyToNavToEmailVerify: Boolean,
//    resetReadyToNavToEmailVerify: () -> Unit,
//    navToEmailVerify: () -> Unit,
    updatedPassword: String,
    onUpdatedPasswordChange: (String) -> Unit,
    submitUpdatedPassword: (String) -> Unit,
    confirmedPassword: String,
    onConfirmedPasswordChange: (String) -> Unit,
    signOut: () -> Unit,
    readyToNavToSignIn: Boolean,
    resetReadyToNavToSignIn: () -> Unit,
    navToSignIn: () -> Unit,
    deleteAccount: () -> Unit,
    onBackNav: () -> Unit
){
    if (readyToNavToSignIn){
        resetReadyToNavToSignIn()
        navToSignIn()
    }
//    if (readyToNavToEmailVerify){
//        resetReadyToNavToEmailVerify()
//        navToEmailVerify()
//    }
    Scaffold(
        topBar = {
            CharTrackerTopBar(
                title =  stringResource(id = R.string.settings),
                onBackNav = onBackNav,
                actionButtons = {})
        }
    ) {
            paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(paddingValue)
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "Settings Screen" }
        ) {
            // change email
            TextEntryHolder(
                title = R.string.update_email,
                label = R.string.update_email_hint,
                text = updatedEmail,
                onTyping = {newInput -> onUpdatedEmailChange(newInput)}
            )
            Button(onClick = { submitUpdatedEmail(updatedEmail) }) {
                Text(text = stringResource(id = R.string.update_email))
            }
            
            // change password
            TextEntryHolder(
                title = R.string.update_password,
                label = R.string.update_password_hint,
                text = updatedPassword,
                onTyping = {newInput -> onUpdatedPasswordChange(newInput)}
            )
            TextEntryHolder(
                title = R.string.password_confirm,
                label = R.string.password_confirm_hint,
                text = confirmedPassword,
                onTyping = {newInput -> onConfirmedPasswordChange(newInput)}
            )
            if (updatedPassword != confirmedPassword){
                Text(
                    text = stringResource(id = R.string.password_mismatch),
                    color = Color.Red,
                    style = MaterialTheme.typography.titleLarge)
            }
            Button(onClick = {
                if (updatedPassword != confirmedPassword) {
                    submitUpdatedPassword(updatedPassword)
                }
            }
            ) {
                Text(text = stringResource(id = R.string.update_password))
            }
            
            // sign out
            Button(onClick = { signOut() }) {
                Text(text = stringResource(id = R.string.sign_out))
            }
            
            // delete account
            Button(onClick = { deleteAccount() }) {
                Text(text = stringResource(id = R.string.delete_account))
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
fun PreviewSettingScreen(){
    var updateEmail by remember { mutableStateOf("") }
    var updatePassword by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    CharTrackerTheme {
        Surface {
            SettingsScreen(
                updatedEmail = updateEmail,
                onUpdatedEmailChange = {new -> updateEmail = new},
                submitUpdatedEmail = {},
                updatedPassword = updatePassword,
                onUpdatedPasswordChange = {new -> updatePassword = new},
                submitUpdatedPassword = {},
                confirmedPassword = confirmedPassword,
                onConfirmedPasswordChange = {new -> confirmedPassword = new},
                signOut = { /*TODO*/ },
                readyToNavToSignIn = false,
                resetReadyToNavToSignIn = { /*TODO*/ },
                navToSignIn = { /*TODO*/ },
                deleteAccount = { /*TODO*/ },
                onBackNav = {}
            )
        }
    }
}