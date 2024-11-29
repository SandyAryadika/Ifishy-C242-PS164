package com.ifishy.utils

sealed class ResponseState<out R> {

    data class Success<out R>(val data: R): ResponseState<R>()
    data class Error(val message: String): ResponseState<Nothing>()
    data object Loading:ResponseState<Nothing>()

}