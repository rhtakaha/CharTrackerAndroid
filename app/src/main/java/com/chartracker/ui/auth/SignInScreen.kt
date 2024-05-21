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
import com.chartracker.database.UserDBInterface
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.viewmodels.auth.SignInViewModel
import com.chartracker.viewmodels.auth.SignInViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun SignInScreen(
    userDB: UserDBInterface,
    navToSignUp: () -> Unit,
    navToStories: () -> Unit,
    navToEmailVerify: (String) -> Unit,
    signInViewModel: SignInViewModel = viewModel(factory = SignInViewModelFactory(userDB = userDB))
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
        unverifiedEmail = signInViewModel.unverifiedEmail.value,
        resetUnverifiedEmail = {signInViewModel.resetUnverifiedEmail()},
        navToStories = navToStories,
        navToEmailVerify = navToEmailVerify,
        onPasswordResetClick = {signInViewModel.sendPasswordResetEmail(signInViewModel.email.value)}
    )
}

/* for previewing*/
@OptIn(ExperimentalCoroutinesApi::class)
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
    unverifiedEmail: Boolean,
    resetUnverifiedEmail: () -> Unit,
    navToStories: () -> Unit,
    navToEmailVerify: (String) -> Unit,
    onPasswordResetClick: (String) -> Unit,
){
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var missingEmailOrPassword by remember {
        mutableStateOf(false)
    }
    var missingEmail by remember {
        mutableStateOf(false)
    }
    if (signedIn){
        //event for navigation on sign in (verified)
        resetSignedIn()
        navToStories()
    }
    if (unverifiedEmail){
        // event for navigation to email verify if unverified
        // from there it goes to stories like usual
        resetUnverifiedEmail()
        navToEmailVerify(email)
    }
    Scaffold(
        snackbarHost ={ SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValue ->
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
fun PreviewSignInScreen(){

    CharTrackerTheme {
        Surface {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var invalidCredentials by remember { mutableStateOf(false) }
            var emailSent by remember { mutableStateOf(false) }
            var unverified by remember { mutableStateOf(false) }
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
                navToEmailVerify = {},
                emailSent = emailSent,
                resetEmailSent = {emailSent = !emailSent},
                unverifiedEmail = unverified,
                resetUnverifiedEmail = {unverified = !unverified},
                onPasswordResetClick = {}
            )
        }
    }
}