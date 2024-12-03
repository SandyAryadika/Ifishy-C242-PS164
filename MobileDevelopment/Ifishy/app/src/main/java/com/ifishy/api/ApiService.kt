package com.ifishy.api

import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.SignUpRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.SignUpResponse
import com.ifishy.data.model.comments.AddCommentResponse
import com.ifishy.data.model.comments.CommentByIdResponse
import com.ifishy.data.model.comments.CommentsResponse
import com.ifishy.data.model.comments.request.AddCommentRequest
import com.ifishy.data.model.community.response.CommunityDetailResponse
import com.ifishy.data.model.community.response.CommunityResponse
import com.ifishy.data.model.profile.response.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body userData: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body userData: SignUpRequest): SignUpResponse

    @GET("profile/{email}")
    suspend fun getProfile(@Header("Authorization") token: String,@Path("email") email:String): ProfileResponse

    @GET("community/posts")
    suspend fun getPosts(@Header("Authorization") token: String): CommunityResponse

    @GET("community/posts/{id}")
    suspend fun getPostById(@Header("Authorization") token: String,@Path("id") id:Int): CommunityDetailResponse

    @GET("community/posts/{id}/comments")
    suspend fun getAllComments(@Header("Authorization") token:String,@Path("id") postId:Int): CommentsResponse

    @GET("community/comments/{id}")
    suspend fun getCommentById(@Header("Authorization") token: String,@Path("id") commentsId:Int): CommentByIdResponse

    @POST("community/posts/{id}/comments")
    suspend fun addComments(@Header("Authorization") token:String,@Path("id") postId:Int,@Body content:AddCommentRequest): AddCommentResponse

    @POST("comments/{id}/reply")
    suspend fun addReply(@Header("Authorization") token: String,@Path("id") commentId: Int, @Body content: AddCommentRequest): AddCommentResponse

}