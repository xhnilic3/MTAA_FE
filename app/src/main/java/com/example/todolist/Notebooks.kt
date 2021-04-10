package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Notebooks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebooks)

        findViewById<Button>(R.id.btnBack).setOnClickListener{
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }

        findViewById<Button>(R.id.btnCreateNotebook).setOnClickListener{
            val intent = Intent(this, CreateNotebook::class.java);
            startActivity(intent);
        }
    }


}