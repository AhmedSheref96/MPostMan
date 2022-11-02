package com.el3asas.mpostman.utils

import io.ktor.client.statement.*

sealed class MResponse {
    data class Success(val response: HttpResponse): MResponse()
    data class Error(val errorMsg:String): MResponse()
}
