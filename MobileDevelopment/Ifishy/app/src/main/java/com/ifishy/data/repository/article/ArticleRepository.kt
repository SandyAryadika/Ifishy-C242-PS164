package com.ifishy.data.repository.article

import com.ifishy.api.ApiService
import com.ifishy.data.model.auth.response.CommunityResponse

interface ArticleRepository {
    suspend fun fetchPosts(token: String): CommunityResponse
}

class ArticleRepositoryImpl(private val apiService: ApiService) : ArticleRepository {

    override suspend fun fetchPosts(token: String): CommunityResponse {
        return apiService.getPosts(token)
    }
}