package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.auth.response.CommunityResponse
import com.ifishy.data.repository.article.ArticleRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ArticleViewModel @Inject constructor(@Named ("CommunityRepository") private val articleRepository: ArticleRepository) : ViewModel() {

    val postsCommunityResponse: MutableLiveData<SingleEvent<ResponseState<CommunityResponse>>> =
        MutableLiveData()

    fun fetchPosts(token: String) {
        viewModelScope.launch {
            postsCommunityResponse.value = SingleEvent(ResponseState.Loading)

            try {
                val response = articleRepository.fetchPosts(token)
                postsCommunityResponse.postValue(SingleEvent(ResponseState.Success(response)))
            } catch (e: Exception){
                postsCommunityResponse.postValue(
                    SingleEvent(ResponseState.Error("Failed to fetch posts: ${e.message}"))
                )
            }
        }
    }
}