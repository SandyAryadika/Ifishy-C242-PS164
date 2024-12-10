package com.ifishy.data.repository.community

import android.app.Application
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.api.ApiService
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.comments.MessageResponse
import com.ifishy.data.model.comments.CommentByIdResponse
import com.ifishy.data.model.comments.CommentsResponse
import com.ifishy.data.model.comments.request.AddCommentRequest
import com.ifishy.data.model.community.response.AddPostResponse
import com.ifishy.data.model.community.response.CommunityDetailResponse
import com.ifishy.data.model.community.response.CommunityResponse
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface CommunityRepository {
    suspend fun getAllPosts(token: String): ResponseState<CommunityResponse>
    suspend fun getPostById(token: String,id:Int): ResponseState<CommunityDetailResponse>
    suspend fun getAllComments(token: String,id: Int): ResponseState<CommentsResponse>
    suspend fun getCommentById(token: String,id: Int): ResponseState<CommentByIdResponse>
    suspend fun addComment(token: String,id:Int,content:String): SingleEvent<ResponseState<MessageResponse>>
    suspend fun addReply(token: String,id: Int,content: String): SingleEvent<ResponseState<MessageResponse>>
    suspend fun addUpvote(token: String,id: Int): SingleEvent<ResponseState<MessageResponse>>
    suspend fun addDownVote(token: String,id: Int): SingleEvent<ResponseState<MessageResponse>>
    suspend fun likePost(token: String,id: Int): SingleEvent<ResponseState<MessageResponse>>
    suspend fun unLikePost(token: String,id: Int): SingleEvent<ResponseState<MessageResponse>>
    suspend fun uploadPost(token: String,title: RequestBody,content: RequestBody,image:MultipartBody.Part): SingleEvent<ResponseState<AddPostResponse>>
}

class CommunityRepositoryImpl @Inject constructor(private val apiService: ApiService,@ApplicationContext private val context: Application) : CommunityRepository {

    override suspend fun getAllPosts(token: String): ResponseState<CommunityResponse> {
        return try {
            val response = apiService.getPosts("Bearer $token")
            ResponseState.Success(response)
        } catch (e: IOException) {
            ResponseState.Error(context.getString(R.string.no_internet))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        }catch (e: IllegalStateException){
            ResponseState.Error(context.getString(R.string.internal_server_error))
        }
    }

    override suspend fun getPostById(token: String, id: Int): ResponseState<CommunityDetailResponse> {
        return try {
            val response = apiService.getPostById("Bearer $token", id)
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

    override suspend fun getAllComments(token: String, id: Int): ResponseState<CommentsResponse> {
        return try {
            val response = apiService.getAllComments("Bearer $token",id)
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

    override suspend fun getCommentById(
        token: String,
        id: Int
    ): ResponseState<CommentByIdResponse> {
       return try {
           val response = apiService.getCommentById("Bearer $token",id)
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

    override suspend fun addComment(
        token: String,
        id: Int,
        content: String
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try{
            val response = apiService.addComments("Bearer $token",id, AddCommentRequest(content = content))
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun addReply(
        token: String,
        id: Int,
        content: String
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try{
            val response = apiService.addReply("Bearer $token", id, AddCommentRequest(content))
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun addUpvote(
        token: String,
        id: Int
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try{
            val response = apiService.addUpvote("Bearer $token",id)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun addDownVote(
        token: String,
        id: Int
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try {
            val response = apiService.addDownVote("Bearer $token",id)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun likePost(
        token: String,
        id: Int
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try{
            val response = apiService.likePost("Bearer $token", id)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun unLikePost(
        token: String,
        id: Int
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try{
            val response = apiService.unLikePost("Bearer $token", id)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun uploadPost(
        token: String,
        title: RequestBody,
        content: RequestBody,
        image: MultipartBody.Part
    ): SingleEvent<ResponseState<AddPostResponse>> {
        return try{
            val response = apiService.uploadPost("Bearer $token",title, content, image)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }
}