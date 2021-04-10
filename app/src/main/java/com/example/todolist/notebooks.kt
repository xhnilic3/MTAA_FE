package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class notebooks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebooks)

        val btn = findViewById(R.id.btnMakeNotebook) as Button;
        btn.setOnClickListener{
            System.out.println("Bruuuh");
        }
    }


}