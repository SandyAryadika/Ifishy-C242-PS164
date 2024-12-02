package com.ifishy.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifishy.data.preference.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val language: LiveData<String> = userPreferences.getLanguage().asLiveData()
    val notificationEnabled: LiveData<Boolean> = userPreferences.isNotificationEnabled().asLiveData()
    val themeDark: LiveData<Boolean> = userPreferences.isThemeDark().asLiveData()

    fun saveSettings(language: String, notificationEnabled: Boolean, themeDark: Boolean) {
        viewModelScope.launch {
            userPreferences.saveSettings(language, notificationEnabled, themeDark)
        }
    }
}