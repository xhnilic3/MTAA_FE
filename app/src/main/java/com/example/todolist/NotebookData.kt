//package com.example.todolist
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//
//data class NotebookData(val id : Int, val imageResource: Int, var text: String)

package com.example.todolist

import android.widget.ImageView

data class NotebookData (
        var notebookId:Int,
        var notebookImage:Int,
        var notebookName:String,
        var notebookLabel:String
)