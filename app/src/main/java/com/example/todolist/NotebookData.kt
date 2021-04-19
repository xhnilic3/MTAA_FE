package com.example.todolist

import android.widget.ImageView
import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotebookData (
        val notebook_id:Int,
        val creator_id:Int,
        val creator_date:String,
        val notebook_type:Int,
        val notebook_name:String,
        val label:String?,
        val notebook_color:String,
        val update_date:String,
        val collaborator_id:Int?
)