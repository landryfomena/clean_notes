package com.kola.cleannotes.business.data.network

sealed class ApiResult <out T>{
    data class  Sucess<out T>(val value:T):ApiResult<T>()
    data class GenericError(
        val code:Int?=null,
        val errorMessage:String?=null
    ):ApiResult<Nothing>()
    object NetworkError: ApiResult<Nothing>()

}