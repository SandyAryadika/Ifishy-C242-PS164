package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.SignUpResponse
import com.ifishy.data.repository.auth.AuthRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(@Named("AuthRepository") private val authRepository: AuthRepository) : ViewModel() {

    val loginResponse : MutableLiveData<SingleEvent<ResponseState<LoginResponse>>> = MutableLiveData()
    val signUpResponse : MutableLiveData<ResponseState<SignUpResponse>> = MutableLiveData()

    fun userLogin(userData: LoginRequest){
        viewModelScope.launch {
            loginResponse.value = SingleEvent(ResponseState.Loading)

            val response = authRepository.login(userData)
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