package com.example.todolist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_notes.*

class NotebookActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val NotebookList = generateDummyList(10)

        recyclerView.adapter = NotebookAdapter(NotebookList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun generateDummyList(size: Int): List<NotebookItem> {
        val list = ArrayList<NotebookItem>()

        for (i in 0 until size) {
            val drawable = R.drawable.ic_android

            val item = NotebookItem(drawable, "Item $i")
            list += item
        }

        return list
    }


}