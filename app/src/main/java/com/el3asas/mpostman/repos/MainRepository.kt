package com.el3asas.mpostman.repos

import androidx.compose.runtime.MutableState
import com.el3asas.data.network.home.HomeService
import com.el3asas.mpostman.utils.MResponse
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(private val homeService: HomeService) {
    suspend fun sendGetApi(
        url: String,
        params: StringValues? = null,
        mHeaders: StringValues? = null
    ): MResponse =
        withContext(Dispatchers.IO) {
            try {
                MResponse.Success(homeService.sendGetApi(url, params, mHeaders))
            } catch (e: Exception) {
                MResponse.Error(e.message.toString())
            }
        }

    suspend fun sendPostApi(
        url: String,
        params: StringValues? = null,
        mHeaders: StringValues? = null,
        mBody: Any? = null
    ): MResponse =
        withContext(Dispatchers.IO) {
            try {
                MResponse.Success(homeService.sendPostApi(url, params, mHeaders, mBody))
            } catch (e: Exception) {
                MResponse.Error(e.message.toString())
            }
        }

}