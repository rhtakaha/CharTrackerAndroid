package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.chartracker.ui.components.TextAndContentHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.database.UserDBInterface
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.viewmodels.auth.EmailVerifyViewModel
import com.chartracker.viewmodels.auth.EmailVerifyViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@Composable
fun EmailVerifyScreen(
    navToStories: () -> Unit,
    userDB: UserDBInterface,
    userEmail: String,
    emailVerifyViewModel: EmailVerifyViewModel = viewModel(factory = EmailVerifyViewModelFactory(userDB))
){
    EmailVerifyScreen(
        email = userEmail,
        updatedEmail = emailVerifyViewModel.updatedEmail.value,
        onUpdatedEmailChange = {newInput -> emailVerifyViewModel.updateUpdatedEmail(newInput)},
        submitUpdatedEmail = {newEmail -> emailVerifyViewModel.updateUserEmail(newEmail)},
        updateEmailVerificationSent = emailVerifyViewModel.updateEmailVerificationSent.value,
        resetUpdateEmailVerificationSent = { emailVerifyViewModel.resetUpdateEmailVerificationSent() },
        invalidUser = emailVerifyViewModel.invalidUser.value,
        resetInvalidUser = { emailVerifyViewModel.resetInvalidUser() },
        sendEmail = { emailVerifyViewModel.sendVerificationEmail()},
        checkVerification = {emailVerifyViewModel.isEmailVerified()},
        failedReload = emailVerifyViewModel.failedReload.value,
        resetFailedReload = {emailVerifyViewModel.resetFailedReload()},
        emailSent = emailVerifyViewModel.emailSent.value,
        resetEmailSent = { emailVerifyViewModel.resetEmailSent() },
        navToStories = navToStories)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun EmailVerifyScreen(
    email: String,
    updatedEmail: String,
    onUpdatedEmailChange: (String) -> Unit,
    submitUpdatedEmail: (String) -> Unit,
    updateEmailVerificationSent: Boolean,
    resetUpdateEmailVerificationSent: () -> Unit,
    invalidUser: Boolean,
    resetInvalidUser: () -> Unit,
    sendEmail: () -> Unit,
    checkVerification: () -> Boolean,
    failedReload: Boolean,
    resetFailedReload: () -> Unit,
    emailSent: Boolean,
    resetEmailSent: () -> Unit,
    navToStories: () -> Unit
){
    var missingEmail by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val notVerifiedMessage = stringResource(id = R.string.not_verified)
    Scaffold(
        snackbarHost ={SnackbarHost(hostState = snackbarHostState) },
        topBar = { CharTrackerTopBar(actionButtons = {}) }
    ) {paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(paddingValue)
                .semantics { contentDescription = "EmailVerify Screen" }){
            TextAndContentHolder(
                title = R.string.email_verification_instructions,
                body = email)
            Button(onClick = { sendEmail() }) {
                Text(text = stringResource(id = R.string.resend))
            }
            // change email
            TextEntryHolder(
                title = R.string.signUp_wrong_email,
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
                Text(stringResource(id = R.string.change_email))
            }
            Button(onClick = {
                if(checkVerification()){
                    // if we are verified then navigate
                    navToStories()
                }else{
                    scope.launch {
                        snackbarHostState.showSnackbar(notVerifiedMessage)
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.Im_verified))
            }
        }
        if (failedReload){
            // really shouldn't ever see, but here in case
            MessageDialog(
                message = stringResource(id = R.string.fail_user_reload_message),
                onDismiss = {resetFailedReload()}
            )
        }
        if (emailSent){
            resetEmailSent()
            val message = stringResource(id = R.string.email_sent)
            LaunchedEffect(key1 = Unit){
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
        if (updateEmailVerificationSent){
            MessageDialog(
                message = stringResource(id = R.string.email_update_instructions),
                onDismiss = { resetUpdateEmailVerificationSent() }
            )
        }
        if (invalidUser){
            MessageDialog(
                message = stringResource(id = R.string.invalid_user),
                onDismiss = { resetInvalidUser() }
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
fun PreviewEmailVerifyScreen(){
    var updateEmail by remember { mutableStateOf("") }
    CharTrackerTheme {
        EmailVerifyScreen(
            email = "test@email.com",
            updatedEmail = updateEmail,
            onUpdatedEmailChange = {new -> updateEmail = new},
            submitUpdatedEmail = {},
            updateEmailVerificationSent = false,
            resetUpdateEmailVerificationSent = {},
            invalidUser = false,
            resetInvalidUser = {},
            sendEmail = {},
            checkVerification = { false},
            failedReload = false,
            resetFailedReload = {},
            emailSent = false,
            resetEmailSent = {},
            navToStories = {}
        )
    }
}