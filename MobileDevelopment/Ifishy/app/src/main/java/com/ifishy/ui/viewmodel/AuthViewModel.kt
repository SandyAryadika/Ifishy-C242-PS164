package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.SignUpResponse
import com.ifishy.data.repository.auth.AuthRepository
import com.ifishy.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(@Named("AuthRepository") private val authRepository: AuthRepository) : ViewModel() {

    val loginResponse : MutableLiveData<ResponseState<LoginResponse>> = MutableLiveData()
    val signUpResponse : MutableLiveData<ResponseState<SignUpResponse>> = MutableLiveData()

    fun userLogin(email: String,password:String){
        viewModelScope.launch {
            loginResponse.value = ResponseState.Loading

            val response = authRepository.login(email,password)
            loginResponse.postValue(response)
        }
    }

    fun signUp(username: String,email: String,password: String,confirmPassword:String){
        viewModelScope.launch {
            signUpResponse.value = ResponseState.Loading
            val response = authRepository.signUp(username,email,password,confirmPassword)
            signUpResponse.postValue(response)
        }
    }

}