package com.ifishy.data.repository.article

import android.app.Application
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.api.ApiService
import com.ifishy.data.model.article.ArticleByIdResponse
import com.ifishy.data.model.article.ArticleResponse
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.utils.ResponseState
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface ArticleRepository {
    suspend fun getAllArticle(): ResponseState<ArticleResponse>
    suspend fun getArticleById(id:Int):ResponseState<ArticleByIdResponse>
}

class ArticleRepositoryImpl @Inject constructor(private val apiService: ApiService,@ApplicationContext private val context: Application) : ArticleRepository {
    override suspend fun getAllArticle(): ResponseState<ArticleResponse> {
        return try{
            val response = apiService.getAllArticle()
            ResponseState.Success(response)
        }catch (e: IOException) {
            ResponseState.Error(context.getString(R.string.no_internet))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        }catch (e: IllegalStateException){
            ResponseState.Error(context.getString(R.string.internal_server_error))
        }
    }

    override suspend fun getArticleById(id: Int): ResponseState<ArticleByIdResponse> {
        return try{
            val response = apiService.getArticleById(id)
            ResponseState.Success(response)
        }catch (e: IOException) {
            ResponseState.Error(context.getString(R.string.no_internet))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        }catch (e: IllegalStateException){
            ResponseState.Error(context.getString(R.string.internal_server_error))
        }
    }

}