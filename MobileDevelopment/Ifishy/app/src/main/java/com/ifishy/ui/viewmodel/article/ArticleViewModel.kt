package com.ifishy.ui.viewmodel.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifishy.data.model.article.ArticleByIdResponse
import com.ifishy.data.model.article.ArticleResponse
import com.ifishy.data.repository.article.ArticleRepository
import com.ifishy.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ArticleViewModel @Inject constructor(@Named("ArticleRepository") private val articleRepository: ArticleRepository): ViewModel(){

    val article : MutableLiveData<ResponseState<ArticleResponse>> = MutableLiveData()
    val articleById: MutableLiveData<ResponseState<ArticleByIdResponse>> = MutableLiveData()

    fun getArticle(){
        viewModelScope.launch {
            article.value = ResponseState.Loading

            val response = articleRepository.getAllArticle()
            article.postValue(response)
        }
    }

    fun getArticleById(id:Int){
        viewModelScope.launch {
            articleById.value = ResponseState.Loading

            val response = articleRepository.getArticleById(id)
            articleById.postValue(response)
        }
    }


}