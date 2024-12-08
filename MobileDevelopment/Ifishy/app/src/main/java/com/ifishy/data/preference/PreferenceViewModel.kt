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
    val theme: LiveData<Boolean?> = userPreferences.getTheme().asLiveData()

    fun saveToken(token:String,email: String){
        viewModelScope.launch {
            userPreferences.saveToken(token,email)
        }
    }

    fun saveTheme(isDark: Boolean): Boolean {
        viewModelScope.launch {
            userPreferences.saveTheme(isDark)
        }
        return isDark
    }

    fun clearSession(){
        viewModelScope.launch {
            userPreferences.clearSession()
        }

    }

}