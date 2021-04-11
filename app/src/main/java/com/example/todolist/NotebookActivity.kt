package com.example.todolist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_notes.*

class NotebookActivity: AppCompatActivity() {

    private val notebookList = generateDummyList(20)
    private val adapter = NotebookAdapter(notebookList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)



        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

//        recyclerView.adapter = NotebookAdapter(NotebookList)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.setHasFixedSize(true)
    }

    fun insertItem(view: View){
        val index = notebookList.size
        val newItem = NotebookItem(
            R.drawable.ic_android,
            "New item"
        )

        notebookList.add(index, newItem)
//        adapter.notifyDataSetChanged()
        adapter.notifyItemInserted(index)
    }

    fun removeItem(view: View){
        val index = notebookList.size - 1

        notebookList.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    private fun generateDummyList(size: Int): ArrayList<NotebookItem> {
        val list = ArrayList<NotebookItem>()

        for (i in 0 until size) {
            val drawable = R.drawable.ic_android

            val item = NotebookItem(drawable, "Item $i")
            list += item
        }

        return list
    }


}