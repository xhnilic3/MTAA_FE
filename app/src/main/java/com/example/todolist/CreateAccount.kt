package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException


class CreateAccount : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        findViewById<Button>(R.id.btnCreate).setOnClickListener{
            val name = findViewById<EditText>(R.id.edName).getText().toString();
            val pass = findViewById<EditText>(R.id.edPass).getText().toString();
            val mail = findViewById<EditText>(R.id.edMail).getText().toString();

            if(name.isNotEmpty() and pass.isNotEmpty() and mail.isNotEmpty()){
                val json = "{\"username\": \"$name\", \"password\": \"$pass\", \"email\": \"$mail\"}"

                var body = RequestBody.create(
                    MediaType.parse("application/json"), json
                )

                run("http://10.0.2.2:8000/users/", body)

                body = RequestBody.create(
                    MediaType.parse("application/json"), "{\"username\": \"$name\", \"password\": \"$pass\"}"
                )
                run("http://10.0.2.2:8000/users/auth/login", body)

                System.out.println(json)
            }
        }
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }

    fun run(url: String, Body:RequestBody) {
        val request = Request.Builder()
            .url(url)
            .post(Body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.body()?.string())
            }
        })
    }
}
