package com.ifishy.ui.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.comments.MessageResponse
import com.ifishy.data.model.profile.request.EditProfileRequest
import com.ifishy.data.model.profile.response.ProfileResponse
import com.ifishy.data.model.profile.response.UpdatePhotoProfileResponse
import com.ifishy.data.repository.profile.ProfileRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProfileViewModel @Inject constructor(@Named("ProfileRepository") private val profileRepository: ProfileRepository): ViewModel() {

    val profile : MutableLiveData<ResponseState<ProfileResponse>> = MutableLiveData()
    val editProfile: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val updatePhoto: MutableLiveData<SingleEvent<ResponseState<UpdatePhotoProfileResponse>>> = MutableLiveData()
    var imageProfile: Uri?=null

    fun getProfile(token: String,email:String){
        viewModelScope.launch {
            profile.value = ResponseState.Loading

            val response = profileRepository.getProfile(token,email)
            profile.postValue(response)
        }
    }

    fun editProfile(token: String,userData: EditProfileRequest){
        viewModelScope.launch {
            editProfile.value = SingleEvent(ResponseState.Loading)

            val response = profileRepository.editProfile(token,userData)
            editProfile.postValue(response)
        }
    }

    fun updatePhoto(token: String, image:MultipartBody.Part){
        viewModelScope.launch {
            updatePhoto.value = SingleEvent(ResponseState.Loading)

            val response = profileRepository.updateProfile(token,image)
            updatePhoto.postValue(response)
        }
    }
}