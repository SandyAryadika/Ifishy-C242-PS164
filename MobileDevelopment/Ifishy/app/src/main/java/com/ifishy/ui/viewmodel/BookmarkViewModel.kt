package com.ifishy.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.bookmark.BookmarkRequest
import com.ifishy.data.model.bookmark.BookmarkResponse
import com.ifishy.data.model.comments.MessageResponse
import com.ifishy.data.repository.bookmark.BookmarkRepository
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BookmarkViewModel @Inject constructor(@Named("BookmarkRepository")private val bookmarkRepository: BookmarkRepository): ViewModel() {

    val allBookmark: MutableLiveData<ResponseState<BookmarkResponse>> = MutableLiveData()
    val setBookmark: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()
    val deleteBookmark: MutableLiveData<SingleEvent<ResponseState<MessageResponse>>> = MutableLiveData()

    fun getAllBookmark(token:String){
        viewModelScope.launch {
            allBookmark.value = ResponseState.Loading

            val response = bookmarkRepository.getAllBookmark(token)
            allBookmark.postValue(response)
        }
    }

    fun setBookmark(token: String, item: BookmarkRequest){
        viewModelScope.launch {
            setBookmark.value = SingleEvent(ResponseState.Loading)

            val response = bookmarkRepository.setBookmark(token,item)
            setBookmark.postValue(response)
        }
    }

    fun deleteBookmark(token: String, item: BookmarkRequest){
        viewModelScope.launch {
            deleteBookmark.value = SingleEvent(ResponseState.Loading)

            val response = bookmarkRepository.removeBookmark(token,item)
            deleteBookmark.postValue(response)
        }
    }

}