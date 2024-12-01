package com.ifishy.data.preference

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object PreferenceModule {

    @Provides
    fun provideUserPreference(context: Application): UserPreferences{
        return UserPreferences(context)
    }
}