package com.chartracker.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chartracker.ui.components.TextAndContentHolder
import com.chartracker.ui.theme.CharTrackerTheme
import com.chartracker.R

@Composable
fun EmailVerifyScreen(
    navToUpdateEmail: () -> Unit,
    navToStories: () -> Unit,
    emailVerifyViewModel: EmailVerifyViewModel = viewModel()
){
    EmailVerifyScreen(email = emailVerifyViewModel.user!!.email!!,// might use argument instead?
        sendEmail = { emailVerifyViewModel.sendVerificationEmail()},
        changeEmail = { navToUpdateEmail() },
        checkVerification = {emailVerifyViewModel.isEmailVerified()},
        navToStories = navToStories)
}

@Composable
fun EmailVerifyScreen(
    email: String,
    sendEmail: () -> Unit,
    changeEmail: () -> Unit,
    checkVerification: () -> Boolean,
    navToStories: () -> Unit
){
    Scaffold {paddingValue ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)) {
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
                    //snackbar?
                }
            }) {
                Text(text = stringResource(id = R.string.Im_verified))
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
fun PreviewEmailVerifyScreen(){
    CharTrackerTheme {
        EmailVerifyScreen(
            email = "test@email.com",
            sendEmail = {},
            changeEmail = { /*TODO*/ },
            checkVerification = { -> false},
            navToStories = {})
    }
}