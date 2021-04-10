package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CreateNotebook : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_notebook)

        findViewById<Button>(R.id.btnCancelNotebook).setOnClickListener{
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }

        findViewById<Button>(R.id.btnConfirmNotebook).setOnClickListener{
            val name = findViewById<EditText>(R.id.edNtbName).getText().toString();
        }
    }
}