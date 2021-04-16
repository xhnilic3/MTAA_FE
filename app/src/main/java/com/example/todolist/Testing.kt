package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import okhttp3.ConnectionSpec.CLEARTEXT
import java.io.IOException
import java.util.*

class Testing : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        run("http://10.0.2.2:8000/notebooks")

        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
    }

    fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }
            override fun onResponse(call: Call, response: Response){
                println(response.body()?.string())
            }
        })
    }
}