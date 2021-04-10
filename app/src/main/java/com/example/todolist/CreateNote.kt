package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CreateNote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        findViewById<Button>(R.id.btnCreateNote).setOnClickListener{
            val name = findViewById<EditText>(R.id.edNoteName)

            System.out.println(name)
        }

        findViewById<Button>(R.id.btnCancelNote).setOnClickListener{
            val intent = Intent(this, Notes::class.java);
            startActivity(intent);
        }
    }
}