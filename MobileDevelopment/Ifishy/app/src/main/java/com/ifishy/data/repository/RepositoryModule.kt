package com.ifishy.data.repository

import android.app.Application
import com.ifishy.api.ApiService
import com.ifishy.data.repository.article.ArticleRepository
import com.ifishy.data.repository.article.ArticleRepositoryImpl
import com.ifishy.data.repository.auth.AuthRepository
import com.ifishy.data.repository.auth.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @Named("AuthRepository")
    fun provideAuthRepo(apiService: ApiService,context: Application): AuthRepository{
        return AuthRepositoryImpl(apiService,context)
    }

    @Provides
    @Named("CommunityRepository")
    fun provideCommunityRepo(apiService: ApiService): ArticleRepository{
        return ArticleRepositoryImpl(apiService)
    }
}