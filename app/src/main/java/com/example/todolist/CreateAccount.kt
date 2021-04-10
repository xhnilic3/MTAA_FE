package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CreateAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        findViewById<Button>(R.id.btnCreate).setOnClickListener{
            val name = findViewById<EditText>(R.id.edName).getText().toString();
            val pass = findViewById<EditText>(R.id.edPass).getText().toString();
            val mail = findViewById<EditText>(R.id.edMail).getText().toString();

            System.out.println(name);
            System.out.println(pass);
            System.out.println(mail);
        }
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }
}
