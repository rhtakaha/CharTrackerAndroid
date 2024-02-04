package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
    navToStories: () -> Unit,
    onPasswordResetClick: (String) -> Unit,
){
    if (signedIn){
        //event for navigation
        navToStories()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "SignIn Screen" }) {
        Text(text = stringResource(id = R.string.SignIn))
        TextEntryHolder(
            title = R.string.email,
            label = R.string.emailHint,
            text = email,
            onTyping = { newInput -> onEmailType(newInput) },
            isEmail = true)
        TextEntryHolder(
            title = R.string.password,
            label = R.string.passwordHint,
            text = password,
            onTyping = { newInput -> onPasswordType(newInput)},
            isPassword = true)
        Button(onClick = {onSignInClick(email, password) }) {
            Text(text = stringResource(id = R.string.SignIn))
        }
        Button(onClick = { onSignUpClick() }) {
            Text(text = stringResource(id = R.string.SignUp))
        }
        Button(onClick = { onPasswordResetClick(email) }) {
            Text(text = stringResource(id = R.string.password_reset))
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
            SignInScreen(
                email = email,
                onEmailType = {newInput -> email = newInput},
                password = password,
                onPasswordType = {newInput -> password = newInput},
                onSignInClick = { _, _ -> },
                onSignUpClick = { /*TODO*/ },
                signedIn = false,
                navToStories = {},
                onPasswordResetClick = {}
            )
        }
    }
}