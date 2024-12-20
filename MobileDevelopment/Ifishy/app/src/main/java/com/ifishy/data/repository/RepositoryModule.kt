package com.ifishy.data.repository

import android.app.Application
import com.ifishy.api.ApiService
import com.ifishy.data.repository.article.ArticleRepository
import com.ifishy.data.repository.article.ArticleRepositoryImpl
import com.ifishy.data.repository.auth.AuthRepository
import com.ifishy.data.repository.auth.AuthRepositoryImpl
import com.ifishy.data.repository.bookmark.BookmarkRepository
import com.ifishy.data.repository.bookmark.BookmarkRepositoryImpl
import com.ifishy.data.repository.community.CommunityRepository
import com.ifishy.data.repository.community.CommunityRepositoryImpl
import com.ifishy.data.repository.history.HistoryRepository
import com.ifishy.data.repository.history.HistoryRepositoryImpl
import com.ifishy.data.repository.profile.ProfileRepository
import com.ifishy.data.repository.profile.ProfileRepositoryImpl
import com.ifishy.data.repository.scan.ScanRepository
import com.ifishy.data.repository.scan.ScanRepositoryImpl
import com.ifishy.ml.MlService
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
    fun provideCommunityRepo(apiService: ApiService,context: Application): CommunityRepository{
        return CommunityRepositoryImpl(apiService,context)
    }

    @Provides
    @Named("ArticleRepository")
    fun provideArticleRepo(apiService: ApiService,context: Application): ArticleRepository{
        return ArticleRepositoryImpl(apiService,context)
    }

    @Provides
    @Named("ProfileRepository")
    fun provideProfileRepo(apiService: ApiService,context: Application): ProfileRepository{
        return ProfileRepositoryImpl(apiService,context)
    }

    @Provides
    @Named("BookmarkRepository")
    fun provideBookmarkRepo(apiService: ApiService,context: Application): BookmarkRepository{
        return BookmarkRepositoryImpl(apiService,context)
    }

    @Provides
    @Named("ScanRepository")
    fun provideScanRepo(mlService: MlService, context: Application): ScanRepository{
        return ScanRepositoryImpl(mlService,context)
    }

    @Provides
    @Named("HistoryRepository")
    fun provideHistoryRepo(apiService: ApiService,context: Application): HistoryRepository {
        return HistoryRepositoryImpl(apiService,context)
    }

}