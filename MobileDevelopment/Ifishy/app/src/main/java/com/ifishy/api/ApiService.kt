package com.ifishy.api

import com.ifishy.data.model.article.ArticleByIdResponse
import com.ifishy.data.model.article.ArticleResponse
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.SignUpRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.SignUpResponse
import com.ifishy.data.model.bookmark.BookmarkRequest
import com.ifishy.data.model.bookmark.BookmarkResponse
import com.ifishy.data.model.comments.MessageResponse
import com.ifishy.data.model.comments.CommentByIdResponse
import com.ifishy.data.model.comments.CommentsResponse
import com.ifishy.data.model.comments.request.AddCommentRequest
import com.ifishy.data.model.community.response.AddPostResponse
import com.ifishy.data.model.community.response.CommunityDetailResponse
import com.ifishy.data.model.community.response.CommunityResponse
import com.ifishy.data.model.profile.response.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body userData: LoginRequest): LoginResponse

    @POST("register")
    suspend fun register(@Body userData: SignUpRequest): SignUpResponse

    @GET("articles")
    suspend fun getAllArticle(): ArticleResponse

    @GET("articles/{id}")
    suspend fun getArticleById(@Path("id") id:Int): ArticleByIdResponse

    @GET("profile/{email}")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): ProfileResponse

    @GET("community/posts")
    suspend fun getPosts(@Header("Authorization") token: String): CommunityResponse

    @GET("community/posts/{id}")
    suspend fun getPostById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): CommunityDetailResponse

    @GET("/bookmarks")
    suspend fun getAllBookmark(@Header("Authorization") token:String): BookmarkResponse

    @POST("/bookmark")
    suspend fun setBookmark(@Header("Authorization") token: String, @Body item: BookmarkRequest): MessageResponse

    @DELETE("/bookmark")
    suspend fun deleteBookmark(@Header("Authorization") token: String, @Body item: BookmarkRequest): MessageResponse

    @GET("community/posts/{id}/comments")
    suspend fun getAllComments(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): CommentsResponse

    @GET("community/comments/{id}")
    suspend fun getCommentById(
        @Header("Authorization") token: String,
        @Path("id") commentsId: Int
    ): CommentByIdResponse

    @POST("community/posts/{id}/comments")
    suspend fun addComments(
        @Header("Authorization") token: String,
        @Path("id") postId: Int,
        @Body content: AddCommentRequest
    ): MessageResponse

    @POST("comments/{id}/reply")
    suspend fun addReply(
        @Header("Authorization") token: String,
        @Path("id") commentId: Int,
        @Body content: AddCommentRequest
    ): MessageResponse

    @POST("community/posts/{id}/upvote")
    suspend fun addUpvote(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): MessageResponse

    @POST("community/posts/{id}/downvote")
    suspend fun addDownVote(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): MessageResponse

    @POST("comments/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") commentId: Int
    ): MessageResponse

    @DELETE("comments/{id}/like")
    suspend fun unLikePost(
        @Header("Authorization") token: String,
        @Path("id") commentId: Int
    ): MessageResponse

    @Multipart
    @POST("community/posts")
    suspend fun uploadPost(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("content") content:RequestBody,
        @Part image: MultipartBody.Part
    ): AddPostResponse
}