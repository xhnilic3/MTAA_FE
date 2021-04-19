package com.example.todolist

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import java.sql.Date

@Keep
@Serializable
data class NoteData (
    val note_id: Int,
    val notebook_id: Int,
    var name: String,
    val create_date: String,
    val update_date: String,
    val note_type: Int,
    var note_content: String
        )


