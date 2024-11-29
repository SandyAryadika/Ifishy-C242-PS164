package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.RegisterResponse
import com.ifishy.data.repository.auth.AuthRepository
import com.ifishy.utils.ResponseState
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val loginResponse : MutableLiveData<ResponseState<LoginResponse>> = MutableLiveData()
    val registerResponse : MutableLiveData<ResponseState<RegisterResponse>> = MutableLiveData()


    fun userLogin(email: String,password:String){
        viewModelScope.launch {
            loginResponse.value = ResponseState.Loading

            val response = authRepository.login(email,password)
            loginResponse.postValue(response)
        }
    }

    fun register(username: String,email: String,password: String,confirmPassword:String){
        viewModelScope.launch {
            registerResponse.value = ResponseState.Loading

            val response = authRepository.register(username,email,password,confirmPassword)
            registerResponse.postValue(response)
        }
    }

}