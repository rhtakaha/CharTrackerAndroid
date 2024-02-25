package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ui.components.TextAndContentHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.MessageDialog
import com.chartracker.viewmodels.auth.EmailVerifyViewModel

@Composable
fun EmailVerifyScreen(
    navToUpdateEmail: () -> Unit,
    navToStories: () -> Unit,
    onBackNav: () -> Unit,
    userEmail: String,
    emailVerifyViewModel: EmailVerifyViewModel = viewModel()
){
    EmailVerifyScreen(email = userEmail,
        sendEmail = { emailVerifyViewModel.sendVerificationEmail()},
        changeEmail = { navToUpdateEmail() },
        checkVerification = {emailVerifyViewModel.isEmailVerified()},
        navToStories = navToStories,
        failedReload = emailVerifyViewModel.failedReload.value,
        resetFailedReload = {emailVerifyViewModel.resetFailedReload()},
        onBackNav= onBackNav)
}

@Composable
fun EmailVerifyScreen(
    email: String,
    sendEmail: () -> Unit,
    changeEmail: () -> Unit,
    checkVerification: () -> Boolean,
    failedReload: Boolean,
    resetFailedReload: () -> Unit,
    navToStories: () -> Unit,
    onBackNav: () -> Unit
){
    Scaffold(topBar = { CharTrackerTopBar(onBackNav=onBackNav) {} }) {paddingValue ->
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
            Button(onClick = { changeEmail() }) {
                Text(stringResource(id = R.string.change_email))
            }
            Button(onClick = {
                if(checkVerification()){
                    // if we are verified then navigate
                    navToStories()
                }else{
                    //UNSURE - snackbar?
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
    CharTrackerTheme {
        EmailVerifyScreen(
            email = "test@email.com",
            sendEmail = {},
            changeEmail = { /*TODO*/ },
            checkVerification = { -> false},
            navToStories = {},
            failedReload = false,
            resetFailedReload = {},
            onBackNav = {})
    }
}