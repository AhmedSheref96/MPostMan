package com.el3asas.models

import kotlinx.serialization.Serializable

@Serializable
data class ParamModel(
    var name: String,
    var value: String
)