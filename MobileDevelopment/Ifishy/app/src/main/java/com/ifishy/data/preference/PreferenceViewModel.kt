package com.ifishy.data.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(private val userPreferences: UserPreferences): ViewModel() {

    val token: LiveData<String> = userPreferences.readToken().asLiveData()
    val isAlreadyLogin: LiveData<Boolean> = userPreferences.alreadyLogin().asLiveData()
    val email: LiveData<String> = userPreferences.getUserEmail().asLiveData()

    fun saveToken(token:String,email: String){
        viewModelScope.launch {
            userPreferences.saveToken(token,email)
        }
    }

}