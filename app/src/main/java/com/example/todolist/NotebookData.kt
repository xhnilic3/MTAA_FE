package com.example.todolist

import android.widget.ImageView
import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotebookData (
        var notebook_id:Int,
        var creator_id:Int,
        var creator_date:String,
        var notebook_type:Int,
        var notebook_name:String,
        var label:String?,
        var notebook_color:String,
        var update_date:String,
        var collaborator_id:Int?
)