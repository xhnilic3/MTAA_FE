package com.example.todolist

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class User(var userName: String, var email: String, var issual_time: Int, var expire_time: Int)
