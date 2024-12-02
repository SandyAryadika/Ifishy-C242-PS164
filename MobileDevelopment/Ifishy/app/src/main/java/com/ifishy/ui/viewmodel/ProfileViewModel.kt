package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.profile.response.ProfileResponse
import com.ifishy.data.repository.profile.ProfileRepository
import com.ifishy.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProfileViewModel @Inject constructor(@Named("ProfileRepository") private val profileRepository: ProfileRepository): ViewModel() {

    val profile : MutableLiveData<ResponseState<ProfileResponse>> = MutableLiveData()

    fun getProfile(token: String,email:String){
        viewModelScope.launch {
            profile.value = ResponseState.Loading

            val response = profileRepository.getProfile(token,email)
            profile.postValue(response)
        }
    }
}