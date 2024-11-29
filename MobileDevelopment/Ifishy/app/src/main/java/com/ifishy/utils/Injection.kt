package com.ifishy.utils

import android.app.Application
import com.ifishy.api.adwd.ApiClient
import com.ifishy.data.repository.auth.AuthRepository

object Injection {

    fun authRepository(context: Application): AuthRepository{
        val apiService = ApiClient.apiService
        return AuthRepository.getInstance(apiService,context)
    }

}