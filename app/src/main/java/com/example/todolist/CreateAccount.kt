package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            val name = findViewById<EditText>(R.id.edName).text.toString()
            val pass = findViewById<EditText>(R.id.edPass).text.toString()
            val mail = findViewById<EditText>(R.id.edMail).text.toString()



            if(name.isNotEmpty() and pass.isNotEmpty() and mail.isNotEmpty() and Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                //Fetching data from input fields
                val json = "{\"username\": \"$name\", \"password\": \"$pass\", \"email\": \"$mail\"}"

                //Preparing post body
                val body = RequestBody.create(
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
                                //Assigning token to global class
                                CurrentUser.token = Json.decodeFromString(response.body()?.string().toString())
                                val intent = Intent(this@CreateAccount, NotebookActivity::class.java)
                                startActivity(intent)
                            }
                        })
                    }
                })



            }
            else{
                this@CreateAccount.runOnUiThread {
                    Toast.makeText(this@CreateAccount, "Wrong credentials!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
