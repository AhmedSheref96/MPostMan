package com.el3asas.mpostman

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.el3asas.mpostman.repos.MainRepository
import com.el3asas.mpostman.ui.home.FORM_DATA_BODY_TYPE
import com.el3asas.mpostman.ui.home.RAW_BODY_TYPE
import com.el3asas.mpostman.ui.home.ui_states.ParamModelUiState
import com.el3asas.mpostman.utils.MResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    val selectedRequestType = mutableStateOf("GET")
    val httpRequestResponse = mutableStateOf("")
    val baseUrl = mutableStateOf("")
    val pathUrl = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    var paramsValues = mutableStateListOf(mutableStateOf(ParamModelUiState()))
    var bodySelectedType = mutableStateOf(RAW_BODY_TYPE)
    val formDataBodyValues = mutableStateListOf(mutableStateOf(ParamModelUiState()))
    val rawBodyValues = mutableStateOf("")

    private fun sendGetApiRequest() {
        viewModelScope.launch {
            isLoading.value = true
            httpRequestResponse.value =
                when (val response = repository.sendGetApi(
                    url = baseUrl.value + pathUrl.value,
                    params = StringValues.build {
                        paramsValues.forEach {
                            append(it.value.name.value, it.value.value.value)
                        }
                    })) {
                    is MResponse.Success -> response.response.call.response.body()
                    is MResponse.Error -> response.errorMsg
                }
            isLoading.value = false
        }
    }

    private fun sendPostApiRequest() {
        viewModelScope.launch {
            isLoading.value = true
            val mBody: Any? = when (bodySelectedType.value) {
                RAW_BODY_TYPE -> {
                    rawBodyValues.value
                }
                FORM_DATA_BODY_TYPE -> {
                    val map = mutableMapOf<String, String>()
                    formDataBodyValues.map {
                        map[it.value.name.value] = it.value.value.value
                    }
                    Gson().toJson(map)
                }
                else -> {
                    null
                }
            }

            httpRequestResponse.value =
                when (val response =
                    repository.sendPostApi(url = baseUrl.value + pathUrl.value, mBody = mBody)) {
                    is MResponse.Success -> response.response.call.response.body()
                    is MResponse.Error -> response.errorMsg
                }
            isLoading.value = false
        }
    }

    fun sendRequest() {
        when (selectedRequestType.value) {
            "POST" -> sendPostApiRequest()
            "GET" -> sendGetApiRequest()
        }
    }

}