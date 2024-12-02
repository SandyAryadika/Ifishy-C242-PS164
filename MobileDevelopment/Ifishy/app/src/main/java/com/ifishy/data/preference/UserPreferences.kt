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

}