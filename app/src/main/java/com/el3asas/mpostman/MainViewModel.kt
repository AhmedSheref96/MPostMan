package com.el3asas.mpostman

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.el3asas.models.ParamModel
import com.el3asas.mpostman.repos.MainRepository
import com.el3asas.mpostman.utils.MResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {
    val selectedRequestType = mutableStateOf("GET")
    val httpRequestResponse = mutableStateOf("")
    val baseUrl = mutableStateOf("")
    val pathUrl = mutableStateOf("")

    val paramsValues =
        ArrayList<MutableState<ParamModel>>().apply { add(mutableStateOf(ParamModel())) }

    private fun sendGetApiRequest() {
        viewModelScope.launch {
            httpRequestResponse.value =
                when (val response = repository.sendGetApi(url = baseUrl.value + pathUrl.value)) {
                    is MResponse.Success -> response.response.call.response.body()
                    is MResponse.Error -> response.errorMsg
                }
        }
    }

    private fun sendPostApiRequest() {
        viewModelScope.launch {
            httpRequestResponse.value =
                when (val response = repository.sendPostApi(url = baseUrl.value + pathUrl.value)) {
                    is MResponse.Success -> response.response.call.response.body()
                    is MResponse.Error -> response.errorMsg
                }
        }
    }

    fun sendRequest() {
        when (selectedRequestType.value) {
            "POST" -> sendPostApiRequest()
            "GET" -> sendGetApiRequest()
        }
    }
}