package com.ifishy.ui.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.SignUpRequest
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
    val signUpResponse : MutableLiveData<SingleEvent<ResponseState<SignUpResponse>>> = MutableLiveData()

    fun userLogin(userData: LoginRequest){
        viewModelScope.launch {
            loginResponse.value = SingleEvent(ResponseState.Loading)

            val response = authRepository.login(userData)
            loginResponse.postValue(response)
        }
    }

    fun signUp(userData: SignUpRequest){
        viewModelScope.launch {
            signUpResponse.value = SingleEvent(ResponseState.Loading)
            val response = authRepository.signUp(userData)
            signUpResponse.postValue(response)
        }
    }

}