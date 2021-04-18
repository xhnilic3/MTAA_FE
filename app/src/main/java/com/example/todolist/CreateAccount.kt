package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.IOException


class CreateAccount : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //Create user button listener
        findViewById<Button>(R.id.btnCreate).setOnClickListener{
            val name = findViewById<EditText>(R.id.edName).getText().toString();
            val pass = findViewById<EditText>(R.id.edPass).getText().toString();
            val mail = findViewById<EditText>(R.id.edMail).getText().toString();

            if(name.isNotEmpty() and pass.isNotEmpty() and mail.isNotEmpty()){
                //Fetching data from input fields
                var json = "{\"username\": \"$name\", \"password\": \"$pass\", \"email\": \"$mail\"}"

                //Preparing post body
                var body = RequestBody.create(
                    MediaType.parse("application/json"), json
                )

                //Creating user in database
                var request = Request.Builder()
                    .url("http://10.0.2.2:8000/users/")
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        throw e
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println(response.body()?.string())
                        //Preparing body for login
                        val newBody = RequestBody.create(
                            MediaType.parse("application/json"), "{\"username\": \"$name\", \"password\": \"$pass\"}"
                        )

                        //Fetching jwt
                        request = Request.Builder()
                            .url("http://10.0.2.2:8000/users/auth/login")
                            .post(newBody)
                            .build()

                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                throw e
                            }

                            override fun onResponse(call: Call, response: Response) {
                                //Asigning token to global class
                                CurrentUser.token = Json.decodeFromString<Token>(response.body()?.string().toString())
                                val intent = Intent(this@CreateAccount, MainActivity::class.java);
                                startActivity(intent);
                            }
                        })
                    }
                })



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
                //println(response.body()?.string())
                @Keep
                @Serializable
                data class User(var userName: String, var email: String, var issual_time: Int, var expire_time: Int)
                @Keep
                @Serializable
                data class Token(var user:User, var jwtToken:String, var refreshToken:String)

                var foo = Json.decodeFromString<Token>(response.body()?.string().toString())

                println(foo.user.userName)
            }
        })
    }
}
