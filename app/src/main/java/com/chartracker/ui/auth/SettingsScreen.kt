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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ConnectivityStatus
import com.chartracker.R
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.ReAuthDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.auth.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun SettingsScreen(
    navToSignIn: () -> Unit,
    onBackNav: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel()
){
   SettingsScreen(
       updatedEmail = settingsViewModel.updatedEmail.value,
       onUpdatedEmailChange = {newInput -> settingsViewModel.updateUpdatedEmail(newInput)},
       submitUpdatedEmail = {newEmail -> settingsViewModel.updateUserEmail(newEmail)},
       updateEmailVerificationSent = settingsViewModel.updateEmailVerificationSent.value,
       resetUpdateEmailVerificationSent = { settingsViewModel.resetUpdateEmailVerificationSent() },
       updatedPassword = settingsViewModel.updatedPassword.value,
       onUpdatedPasswordChange = { newInput -> settingsViewModel.updateUpdatedPassword(newInput)},
       submitUpdatedPassword = {newPassword -> settingsViewModel.updatePassword(newPassword)},
       confirmedPassword = settingsViewModel.confirmedPassword.value,
       onConfirmedPasswordChange = { newInput -> settingsViewModel.updateConfirmedPassword(newInput)},
       weakPassword = settingsViewModel.weakPassword.value,
       resetWeakPassword = { settingsViewModel.resetWeakPassword() },
       invalidUser = settingsViewModel.invalidUser.value,
       resetInvalidUser = { settingsViewModel.resetInvalidUser() },
       triggerReAuth = settingsViewModel.triggerReAuth.value,
       resetTriggerReAuth = { settingsViewModel.resetTriggerReAuth() },
       reAuthenticateUser = {input -> settingsViewModel.reAuthUser(input)},
       passwordUpdateSuccess = settingsViewModel.passwordUpdateSuccess.value,
       resetPasswordUpdateSuccess = { settingsViewModel.resetPasswordUpdateSuccess() },
       signOut = { settingsViewModel.signOut() },
       readyToNavToSignIn = settingsViewModel.readyToNavToSignIn.value,
       resetReadyToNavToSignIn = { settingsViewModel.resetReadyToNavToSignIn() },
       navToSignIn = navToSignIn,
       deleteAccount = { settingsViewModel.deleteUser() },
       onBackNav = onBackNav)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SettingsScreen(
    updatedEmail: String,
    onUpdatedEmailChange: (String) -> Unit,
    submitUpdatedEmail: (String) -> Unit,
    updateEmailVerificationSent: Boolean,
    resetUpdateEmailVerificationSent: () -> Unit,
    updatedPassword: String,
    onUpdatedPasswordChange: (String) -> Unit,
    submitUpdatedPassword: (String) -> Unit,
    confirmedPassword: String,
    onConfirmedPasswordChange: (String) -> Unit,
    weakPassword: String,
    resetWeakPassword: () -> Unit,
    invalidUser: Boolean,
    resetInvalidUser: () -> Unit,
    triggerReAuth: Boolean,
    resetTriggerReAuth: () -> Unit,
    reAuthenticateUser: (String) -> Unit,
    passwordUpdateSuccess: Boolean,
    resetPasswordUpdateSuccess: () -> Unit,
    signOut: () -> Unit,
    readyToNavToSignIn: Boolean,
    resetReadyToNavToSignIn: () -> Unit,
    navToSignIn: () -> Unit,
    deleteAccount: () -> Unit,
    onBackNav: () -> Unit
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var missingEmail by remember {
        mutableStateOf(false)
    }
    var passwordMismatch by remember {
        mutableStateOf(false)
    }
    var missingPassword by remember {
        mutableStateOf(false)
    }
    if (readyToNavToSignIn){
        resetReadyToNavToSignIn()
        navToSignIn()
    }
    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CharTrackerTopBar(
                title =  stringResource(id = R.string.settings),
                onBackNav = onBackNav,
                actionButtons = {})
        }
    ) { paddingValue ->
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
                title = R.string.update_email_prompt,
                label = R.string.update_email_hint,
                text = updatedEmail,
                isEmail = true,
                onTyping = {newInput -> onUpdatedEmailChange(newInput)}
            )
            Button(onClick = {
                if (updatedEmail != ""){
                    submitUpdatedEmail(updatedEmail)
                }else{
                    missingEmail = true
                }

            }) {
                Text(text = stringResource(id = R.string.update_email))
            }
            
            // change password
            TextEntryHolder(
                title = R.string.update_password_prompt,
                label = R.string.update_password_hint,
                text = updatedPassword,
                isPassword = true,
                onTyping = {newInput -> onUpdatedPasswordChange(newInput)}
            )
            TextEntryHolder(
                title = R.string.password_confirm,
                label = R.string.password_confirm_hint,
                text = confirmedPassword,
                isPassword = true,
                onTyping = {newInput -> onConfirmedPasswordChange(newInput)}
            )
            Button(onClick = {
                if (updatedPassword != "" && confirmedPassword != ""){
                    if (updatedPassword == confirmedPassword) {
                        submitUpdatedPassword(updatedPassword)
                    }else{
                        passwordMismatch = true
                    }
                }else{
                    missingPassword = true
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
        if (passwordUpdateSuccess){
            MessageDialog(
                message = stringResource(id = R.string.password_update_success),
                onDismiss = { resetPasswordUpdateSuccess() }
            )
        }
        if (triggerReAuth){
            //TODO need to test
            ReAuthDialog(
                confirmReauthentication = reAuthenticateUser,
                onDismiss = {resetTriggerReAuth()}
                )
        }
        if (weakPassword != ""){
            MessageDialog(
                message = weakPassword,
                onDismiss = { resetWeakPassword() }
            )
        }
        if (invalidUser){
            MessageDialog(
                message = stringResource(id = R.string.invalid_user),
                onDismiss = { resetInvalidUser() }
            )
        }
        if (updateEmailVerificationSent){
            MessageDialog(
                message = stringResource(id = R.string.email_update_instructions),
                onDismiss = { resetUpdateEmailVerificationSent() }
            )
        }
        if (missingPassword) {
            MessageDialog(
                message = stringResource(id = R.string.missing_password),
                onDismiss = { missingPassword = false }
            )
        }
        if (passwordMismatch) {
            MessageDialog(
                message = stringResource(id = R.string.password_mismatch),
                onDismiss = { passwordMismatch = false }
            )
        }
        if (missingEmail){
            MessageDialog(
                message = stringResource(id = R.string.missing_email),
                onDismiss = {missingEmail = false})
        }
        ConnectivityStatus(scope, snackbarHostState)
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
                updateEmailVerificationSent = false,
                resetUpdateEmailVerificationSent = {},
                updatedPassword = updatePassword,
                onUpdatedPasswordChange = {new -> updatePassword = new},
                submitUpdatedPassword = {},
                confirmedPassword = confirmedPassword,
                onConfirmedPasswordChange = {new -> confirmedPassword = new},
                weakPassword = "",
                resetWeakPassword = {},
                invalidUser = false,
                resetInvalidUser = {},
                triggerReAuth = false,
                resetTriggerReAuth = {},
                reAuthenticateUser = {_ ->},
                passwordUpdateSuccess = false,
                resetPasswordUpdateSuccess = {},
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