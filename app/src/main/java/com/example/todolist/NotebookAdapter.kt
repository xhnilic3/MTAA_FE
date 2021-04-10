package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notebook_item.view.*

class NotebookAdapter(private val notebookList: List<NotebookItem>): RecyclerView.Adapter<NotebookAdapter.NotebookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notebook_item, parent, false)

        return NotebookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        val currentItem = notebookList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView.text = currentItem.text
    }

    override fun getItemCount() = notebookList.size

    //    itemView will be one instance of our row layout
    class NotebookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//      synthetic property used instead itemView.image_view used instead of findViewById(R.id.image_view)
        val imageView: ImageView = itemView.image_view
        val textView: TextView = itemView.text_view
    }
}