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
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.ui.components.CharTrackerTopBar
import com.chartracker.ui.components.MessageDialog
import com.chartracker.ui.components.TextEntryHolder
import com.chartracker.viewmodels.auth.SignUpViewModel

@Composable
fun SignUpScreen(
    navToEmailVerify: (String) -> Unit,
    onBackNav: () -> Unit,
    signUpViewModel: SignUpViewModel = viewModel()
){
    SignUpScreen(
        email = signUpViewModel.email.value,
        onEmailType = {newInput -> signUpViewModel.updateInputEmail(newInput)},
        password1 = signUpViewModel.password1.value,
        onPassword1Type = {newInput -> signUpViewModel.updateInputPassword1(newInput)},
        password2 = signUpViewModel.password2.value,
        onPassword2Type = {newInput -> signUpViewModel.updateInputPassword2(newInput)},
        onSignUpClick = {email, password -> signUpViewModel.signUpUserWithEmailPassword(email, password)},
        signedIn = signUpViewModel.signedIn.value,
        resetSignedIn = { signUpViewModel.resetSignedIn() },
        signUpErrorMessage = signUpViewModel.signUpErrorMessage.value,
        resetSignUpErrorMessage = { signUpViewModel.resetSignUpErrorMessage() },
        navToVerifyEmail= navToEmailVerify,
        onBackNav = onBackNav
    )
}

@Composable
fun SignUpScreen(
    email: String,
    onEmailType: (String) -> Unit,
    password1: String,
    onPassword1Type: (String) -> Unit,
    password2: String,
    onPassword2Type: (String) -> Unit,
    onSignUpClick: (String, String) -> Unit,
    signedIn: Boolean,
    resetSignedIn: () -> Unit,
    signUpErrorMessage: Any?,
    resetSignUpErrorMessage: () -> Unit,
    navToVerifyEmail: (String) -> Unit,
    onBackNav: () -> Unit
){
    var missingEmailOrPassword by remember {
        mutableStateOf(false)
    }
    var passwordMismatch by remember {
        mutableStateOf(false)
    }
    if (signedIn){
        //event for navigation
        resetSignedIn()
        navToVerifyEmail(email)
    }
    Scaffold(topBar = { CharTrackerTopBar(onBackNav=onBackNav) {} }) { paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(paddingValue)
                .semantics { contentDescription = "SignUp Screen" }){
            Text(text = stringResource(id = R.string.SignUp))
            TextEntryHolder(
                title = R.string.email,
                label = R.string.emailHint,
                text = email,
                onTyping = { newInput -> onEmailType(newInput) },
                isEmail = true)
            TextEntryHolder(
                title = R.string.password,
                label = R.string.passwordHint,
                text = password1,
                onTyping = { newInput -> onPassword1Type(newInput) },
                isPassword = true)
            TextEntryHolder(
                title = R.string.password_confirm,
                label = R.string.password_confirm_hint,
                text = password2,
                onTyping = { newInput -> onPassword2Type(newInput) },
                isPassword = true)
            Button(onClick = {
                if (password1 != "" && password2 != "" && email != "") {
                    if (password1 == password2){
                        onSignUpClick(email, password1)
                    }else{
                        passwordMismatch = true
                    }
                }else{
                    missingEmailOrPassword = true
                }
            })
            {
                Text(text = stringResource(id = R.string.SignUp))
            }
        }
        if (passwordMismatch) {
            MessageDialog(
                message = stringResource(id = R.string.password_mismatch),
                onDismiss = { passwordMismatch = false }
            )
        }
        if (missingEmailOrPassword) {
            MessageDialog(
                message = stringResource(id = R.string.missing_email_password),
                onDismiss = { missingEmailOrPassword = false }
            )
        }
        if (signUpErrorMessage != null){
            if (signUpErrorMessage is String){
                MessageDialog(
                    message = signUpErrorMessage,
                    onDismiss = {resetSignUpErrorMessage()}
                )
            }else if (signUpErrorMessage is Int){
                MessageDialog(
                    message = stringResource(id = signUpErrorMessage),
                    onDismiss = {resetSignUpErrorMessage()}
                )
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
fun PreviewSignUpScreen(){
    CharTrackerTheme {
        Surface {
            var email by remember { mutableStateOf("") }
            var password1 by remember { mutableStateOf("") }
            var password2 by remember { mutableStateOf("") }
            SignUpScreen(
                email = email,
                onEmailType = {newInput -> email = newInput} ,
                password1 = password1,
                onPassword1Type = {newInput -> password1 = newInput} ,
                password2 = password2,
                onPassword2Type = {newInput -> password2 = newInput} ,
                onSignUpClick = { _, _ ->},
                signedIn = false,
                resetSignedIn = {},
                signUpErrorMessage = null,
                resetSignUpErrorMessage = {},
                navToVerifyEmail = {},
                onBackNav = {}
            )
        }
    }
}