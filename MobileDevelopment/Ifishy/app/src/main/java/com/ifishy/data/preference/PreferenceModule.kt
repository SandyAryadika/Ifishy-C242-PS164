package com.ifishy.data.preference

import android.app.Application
import com.ifishy.api.ApiService
import com.ifishy.data.repository.article.ArticleRepository
import com.ifishy.data.repository.article.ArticleRepositoryImpl
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

    @Provides
    fun provideArticleViewModel(apiService: ApiService): ArticleRepository{
        return ArticleRepositoryImpl(apiService)
    }
}