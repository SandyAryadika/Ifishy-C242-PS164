package com.ifishy.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.comments.MessageResponse
import com.ifishy.data.model.comments.CommentByIdResponse
import com.ifishy.data.model.comments.CommentsResponse
import com.ifishy.data.model.community.response.AddPostResponse
import com.ifishy.data.model.community.response.CommunityDetailResponse
import com.ifishy.data.model.community.response.CommunityResponse
import com.ifishy.data.repository.community.CommunityRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CommunityViewModel @Inject constructor(@Named ("CommunityRepository") private val communityRepository: CommunityRepository) : ViewModel() {

    val posts: MutableLiveData<ResponseState<CommunityResponse>> = MutableLiveData()
    val postById: MutableLiveData<ResponseState<CommunityDetailResponse>> = MutableLiveData()
    val comments: MutableLiveData<ResponseState<CommentsResponse>> = MutableLiveData()
    val commentById: MutableLiveData<ResponseState<CommentByIdResponse>> = MutableLiveData()
    val addComment: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val addReply: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val addUpvote: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val addDownVote: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val likePost: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val unLikePost: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    var imagePost: Uri?=null
    val uploadPost: MutableLiveData<SingleEvent<ResponseState<AddPostResponse>>> = MutableLiveData()

    fun getCommentById(token: String,id: Int){
        viewModelScope.launch {
            commentById.value = ResponseState.Loading

            val response = communityRepository.getCommentById(token,id)
            commentById.postValue(response)
        }
    }

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

    fun addComments(token: String,id: Int,content:String){
        viewModelScope.launch {
            addComment.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.addComment(token,id,content)
            addComment.postValue(response)
        }
    }

    fun addReply(token: String,id: Int,content: String){
        viewModelScope.launch {
            addReply.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.addReply(token,id,content)
            addReply.postValue(response)
        }
    }

    fun addUpvote(token: String,id: Int){
        viewModelScope.launch {
            addUpvote.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.addUpvote(token,id)
            addReply.postValue(response)
        }
    }

    fun addDownVote(token: String,id: Int){
        viewModelScope.launch {
            addDownVote.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.addDownVote(token,id)
            addDownVote.postValue(response)
        }
    }

    fun likePost(token: String,id: Int){
        viewModelScope.launch {
            likePost.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.likePost(token,id)
            likePost.postValue(response)
        }
    }

    fun unLikePost(token: String,id: Int){
        viewModelScope.launch {
            unLikePost.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.unLikePost(token,id)
            unLikePost.postValue(response)
        }
    }

    fun uploadPost(token: String,title:RequestBody,content: RequestBody, image: MultipartBody.Part){
        viewModelScope.launch {
            uploadPost.value = SingleEvent(ResponseState.Loading)

            val response = communityRepository.uploadPost(token, title, content, image)
            uploadPost.postValue(response)
        }
    }
}