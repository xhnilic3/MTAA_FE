package com.example.todolist

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Token(var user:User, var jwtToken:String, var refreshToken:String)