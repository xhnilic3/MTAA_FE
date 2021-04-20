package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//            val intent = Intent(this, Notebooks::class.java);
//            startActivity(intent);
//        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            logButton()
        }

        findViewById<Button>(R.id.btnCreateAcc).setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java);
            startActivity(intent);
        }

        findViewById<FloatingActionButton>(R.id.mainSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java);
            startActivity(intent);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logButton() {
        val client = OkHttpClient()
        val name = findViewById<EditText>(R.id.logName).getText().toString();
        val pass = findViewById<EditText>(R.id.logPass).getText().toString();

        val loginBod = RequestBody.create(
            MediaType.parse("application/json"),
            "{\"username\": \"$name\", \"password\": \"$pass\"}"
        )

        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/users/auth/login")
            .post(loginBod)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    //Asigning token to global class
                    CurrentUser.token =
                        Json.decodeFromString<Token>(response.body()?.string().toString())
                    val intent = Intent(this@MainActivity, NotebookActivity::class.java);
                    startActivity(intent);
                }
                else {
                    this@MainActivity.runOnUiThread {
                        Toast.makeText(this@MainActivity, "Wrong credentials!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })


    }
}