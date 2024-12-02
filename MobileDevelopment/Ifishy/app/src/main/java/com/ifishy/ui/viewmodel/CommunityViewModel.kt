package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.comments.CommentsResponse
import com.ifishy.data.model.community.response.CommunityDetailResponse
import com.ifishy.data.model.community.response.CommunityResponse
import com.ifishy.data.repository.community.CommunityRepository
import com.ifishy.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CommunityViewModel @Inject constructor(@Named ("CommunityRepository") private val communityRepository: CommunityRepository) : ViewModel() {

    val posts: MutableLiveData<ResponseState<CommunityResponse>> = MutableLiveData()
    val postById: MutableLiveData<ResponseState<CommunityDetailResponse>> = MutableLiveData()
    val comments: MutableLiveData<ResponseState<CommentsResponse>> = MutableLiveData()

    fun getAllPosts(token: String) {
        viewModelScope.launch {
            posts.value = ResponseState.Loading

            val response = communityRepository.getAllPosts(token)
            posts.postValue(response)

        }
    }

    fun getPostById(token: String,id:Int){
        viewModelScope.launch {
            postById.value = ResponseState.Loading

            val response = communityRepository.getPostById(token,id)
            postById.postValue(response)
        }
    }

    fun getAllComments(token: String,id: Int){
        viewModelScope.launch {
            comments.value = ResponseState.Loading

            val response = communityRepository.getAllComments(token,id)
            comments.postValue(response)
        }
    }

}