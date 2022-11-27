package com.el3asas.mpostman.ui.home.ui_states

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ParamModelUiState(
    var name: MutableState<String> = mutableStateOf(""),
    var value: MutableState<String> = mutableStateOf("")
)
