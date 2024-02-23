package com.chartracker

//import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chartracker.viewmodels.auth.SignInViewModel
import org.junit.Test
//import com.google.common.truth.Truth.assertThat
//import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
class SignInViewModelTest {

    @Test
    fun basic(){
        val vm = SignInViewModel()

        vm.signInWithEmailPassword("rytakahashi97@gmail.com", "pass1234")
//        assertThat(vm.signedIn).isEqualTo(true)
    }
}