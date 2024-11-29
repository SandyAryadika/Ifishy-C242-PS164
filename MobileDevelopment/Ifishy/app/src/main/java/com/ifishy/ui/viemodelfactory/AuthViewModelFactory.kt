package com.ifishy.ui.viemodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifishy.data.repository.auth.AuthRepository
import com.ifishy.ui.viewmodel.AuthViewModel
import com.ifishy.utils.Injection

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory private constructor(private val authRepository: AuthRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(authRepository) as T
        }

        throw IllegalArgumentException ("$modelClass not found")
    }

    companion object{
        @Volatile
        private var INSTANCE: AuthViewModelFactory?=null

        fun getInstance(context: Application): AuthViewModelFactory{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: AuthViewModelFactory(Injection.authRepository(context)).also { INSTANCE = it }
            }
        }
    }

}