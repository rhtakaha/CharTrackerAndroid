package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R
import com.chartracker.ui.components.TextEntryHolder

@Composable
fun SignUpScreen(
    navToEmailVerify: () -> Unit,
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
        navToVerifyEmail= navToEmailVerify
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
    navToVerifyEmail: () -> Unit
){
    if (signedIn){
        //event for navigation
        navToVerifyEmail()
    }
    Scaffold {paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)) {
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
                label = R.string.passwordHint,
                text = password2,
                onTyping = { newInput -> onPassword2Type(newInput) },
                isPassword = true)
            if (password1 != password2){
                Text(
                    text = stringResource(id = R.string.password_mismatch),
                    color = Color.Red,
                    style = MaterialTheme.typography.titleLarge)
            }
            Button(onClick = {
                if (password1 == password2) {
                    onSignUpClick(email, password1)
                }
            })
            {
                Text(text = stringResource(id = R.string.SignUp))
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
                navToVerifyEmail = {}
            )
        }
    }
}