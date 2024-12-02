package com.ifishy.data.preference

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ifishy.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(BuildConfig.USER_PREFERENCE)


class UserPreferences @Inject constructor(private val context: Application) {


    private val TOKEN = stringPreferencesKey("user_token")
    private val ALREADY_LOGIN = booleanPreferencesKey("already_login")
    private val EMAIL = stringPreferencesKey("user_email")


    private val LANGUAGE = stringPreferencesKey("user_language")
    private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    private val THEME_DARK = booleanPreferencesKey("theme_dark")

    suspend fun saveToken(token:String,email: String) {
        context.dataStore.edit { userToken ->
            userToken[ALREADY_LOGIN] = true
            userToken[EMAIL] = email
            userToken[TOKEN] = token
        }
    }

    fun readToken(): Flow<String> {
        return context.dataStore.data.map { token->
            token[TOKEN] ?: ""
        }
    }

    fun getUserEmail(): Flow<String>{
        return context.dataStore.data.map { email->
            email[EMAIL] ?: ""
        }
    }

    fun alreadyLogin(): Flow<Boolean>{
        return context.dataStore.data.map { isAlreadyLogin->
            isAlreadyLogin[ALREADY_LOGIN] ?: false
        }
    }

    suspend fun saveSettings(language: String, notificationEnabled: Boolean, themeDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
            preferences[NOTIFICATION_ENABLED] = notificationEnabled
            preferences[THEME_DARK] = themeDark
        }
    }

    fun getLanguage(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE] ?: "EN(US)"
        }
    }

    fun isNotificationEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[NOTIFICATION_ENABLED] ?: true
        }
    }

    fun isThemeDark(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_DARK] ?: false
        }
    }
}