package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.R
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.auth.SignInViewModel

@Composable
fun SignInScreen(
    navToSignUp: () -> Unit,
    navToStories: () -> Unit,
    signInViewModel: SignInViewModel = viewModel()
){
    SignInScreen(
        email = signInViewModel.email.value,
        onEmailType = {newInput -> signInViewModel.updateInputEmail(newInput)},
        password = signInViewModel.password.value,
        onPasswordType = {newInput -> signInViewModel.updateInputPassword(newInput)},
        onSignInClick = {email, password ->  signInViewModel.signInWithEmailPassword(email, password) },
        onSignUpClick = { navToSignUp() },
        signedIn = signInViewModel.signedIn.value,
        resetSignedIn = { signInViewModel.resetSignedIn() },
        invalidCredentials = signInViewModel.invalidCredentials.value,
        resetInvalidCredentials = { signInViewModel.resetInvalidCredentials() },
        emailSent = signInViewModel.emailSent.value,
        resetEmailSent = { signInViewModel.resetEmailSent() },
        navToStories = navToStories,
        onPasswordResetClick = {signInViewModel.sendPasswordResetEmail(signInViewModel.email.value)}
    )
}

/* for previewing*/
@Composable
fun SignInScreen(
    email: String,
    onEmailType: (String) -> Unit,
    password: String,
    onPasswordType: (String) -> Unit,
    onSignInClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    signedIn: Boolean,
    resetSignedIn: () -> Unit,
    invalidCredentials: Boolean,
    resetInvalidCredentials: () -> Unit,
    emailSent: Boolean,
    resetEmailSent: () -> Unit,
    navToStories: () -> Unit,
    onPasswordResetClick: (String) -> Unit,
){
    var missingEmailOrPassword by remember {
        mutableStateOf(false)
    }
    var missingEmail by remember {
        mutableStateOf(false)
    }
    if (signedIn){
        //event for navigation
        resetSignedIn()
        navToStories()
    }
    Scaffold { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(paddingValue)
                .fillMaxSize()
                .semantics { contentDescription = "SignIn Screen" }) {
            Text(text = stringResource(id = R.string.SignIn))
            TextEntryHolder(
                title = R.string.email,
                label = R.string.emailHint,
                text = email,
                onTyping = { newInput -> onEmailType(newInput) },
                isEmail = true
            )
            TextEntryHolder(
                title = R.string.password,
                label = R.string.passwordHint,
                text = password,
                onTyping = { newInput -> onPasswordType(newInput) },
                isPassword = true
            )
            Button(onClick = {
                if (email != "" && password != "") {
                    onSignInClick(email, password)
                } else {
                    missingEmailOrPassword = true
                }

            }) {
                Text(text = stringResource(id = R.string.SignIn))
            }
            Button(onClick = { onSignUpClick() }) {
                Text(text = stringResource(id = R.string.SignUp))
            }
            Button(onClick = {
                if (email != "") {
                    onPasswordResetClick(email)
                } else {
                    missingEmail = true
                }

            }) {
                Text(text = stringResource(id = R.string.password_reset))
            }

        }
        if (missingEmail) {
            // for password reset where no email entered
            MessageDialog(
                message = stringResource(id = R.string.missing_email),
                onDismiss = { missingEmail = false }
            )
        }
        if (missingEmailOrPassword) {
            // for sign in missing a field
            MessageDialog(
                message = stringResource(id = R.string.missing_email_password),
                onDismiss = { missingEmailOrPassword = false }
            )
        }
        if (invalidCredentials) {
            // for sign in credentials failing
            MessageDialog(
                message = stringResource(id = R.string.invalid_email_password),
                onDismiss = { resetInvalidCredentials() }
            )
        }
        if (emailSent) {
            // when a password reset is invoked on an email
            MessageDialog(
                message = stringResource(id = R.string.password_reset_email_sent),
                onDismiss = { resetEmailSent() }
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
fun PreviewSignInScreen(){

    CharTrackerTheme {
        Surface {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var invalidCredentials by remember { mutableStateOf(false) }
            var emailSent by remember { mutableStateOf(false) }
            SignInScreen(
                email = email,
                onEmailType = {newInput -> email = newInput},
                password = password,
                onPasswordType = {newInput -> password = newInput},
                onSignInClick = { _, _ -> },
                onSignUpClick = { /**/ },
                signedIn = false,
                resetSignedIn = {},
                invalidCredentials = invalidCredentials,
                resetInvalidCredentials = {invalidCredentials = !invalidCredentials},
                navToStories = {},
                emailSent = emailSent,
                resetEmailSent = {emailSent = !emailSent},
                onPasswordResetClick = {}
            )
        }
    }
}