package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.community.response.CommuntyResponse
import com.ifishy.data.repository.community.CommunityRepository
import com.ifishy.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CommunityViewModel @Inject constructor(@Named ("CommunityRepository") private val communityRepository: CommunityRepository) : ViewModel() {

    val posts: MutableLiveData<ResponseState<CommuntyResponse>> = MutableLiveData()

    fun getAllPosts(token: String) {
        viewModelScope.launch {
            posts.value = ResponseState.Loading

            val response = communityRepository.getAllPosts(token)
            posts.postValue(response)

        }
    }
}