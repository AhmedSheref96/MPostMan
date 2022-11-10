package com.el3asas.data.network.home

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import javax.inject.Inject

class HomeService @Inject constructor(private val client: HttpClient) {
    suspend fun sendGetApi(
        url: String,
        params: StringValues? = null,
        mHeaders: StringValues? = null
    ): HttpResponse {
        return client.get(url) {
            url {
                params?.let { it1 -> parameters.appendAll(it1) }
            }
            mHeaders?.let { headers.appendAll(it) }
        }
    }

    suspend fun sendPostApi(
        url: String,
        params: StringValues? = null,
        mHeaders: StringValues? = null,
        mBody: Any? = null
    ): HttpResponse {
        return client.post(url) {
            url {
                params?.let { it1 -> parameters.appendAll(it1) }
            }
            mHeaders?.let { headers.appendAll(it) }
            setBody(mBody)
        }
    }

    suspend fun sendPutApi(
        url: String,
        params: StringValues? = null,
        mHeaders: StringValues? = null,
        mBody: Any? = null
    ): HttpResponse {
        return client.put(url) {

        }
    }
}