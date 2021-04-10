package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class notebookAdapter(
    private val notebookList: MutableList<notebook>
) : RecyclerView.Adapter<notebookAdapter.notebookViewHolder>(){
    class notebookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notebookViewHolder {
        return notebookViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.notebook_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: notebookViewHolder, position: Int) {
        val curNotebook = notebookList[position]

    }

    override fun getItemCount(): Int {
        return notebookList.size;
    }
}