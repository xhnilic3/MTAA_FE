package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

class NoteActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var noteList:ArrayList<NoteData>
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_note)
        /**set List*/
        noteList = ArrayList()
        /**set find Id*/
        addsBtn = findViewById(R.id.addingBtn)
        recv = findViewById(R.id.mRecycler)
        /**set Adapter*/
        noteAdapter = NoteAdapter(this,noteList)
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = noteAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addInfo() }

        //Getting stuff from database
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${CurrentNotebook.id}/notes")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }
            override fun onResponse(call: Call, response: Response){
                val foo = Json.decodeFromString<List<NoteData>>(response.body()?.string().toString())
                println(CurrentNotebook.id)
                for (item in foo) noteList.add(item)
                //Thread handling
                this@NoteActivity.runOnUiThread {
                    noteAdapter.notifyDataSetChanged()
                }
            }
        })


        findViewById<FloatingActionButton>(R.id.ntSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_note_item,null)
        /**set view*/
        val noteName = v.findViewById<EditText>(R.id.noteName)
        val noteLabel = v.findViewById<EditText>(R.id.note_content)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val names = noteName.text.toString()
            val label = noteLabel.text.toString()
            ///----------------------------------------------------------------------------
            val client = OkHttpClient()

            val bod = RequestBody.create(
                MediaType.parse("application/json"), """
                {
                  "name": "$names",
                  "note_type": 1,
                  "note_content": "$label"
                }
                """.trimIndent()
            )

            //Fetching jwt
            val request = Request.Builder()
                .url("http://10.0.2.2:8000/notebooks/${CurrentNotebook.id}/notes/")
                .post(bod)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Fail debug")
                    throw e
                }

                override fun onResponse(call: Call, response: Response) {
                    println(response.code())
                    if(response.code() == 201){
                        //Asigning token to global class
                        val newNote = Json.decodeFromString<NoteData>(response.body()?.string().toString())
                        noteList.add(newNote)
                        //Thread handling
                        this@NoteActivity.runOnUiThread {
                            noteAdapter.notifyDataSetChanged()
                        }

                    }


                }
            })
            Toast.makeText(this@NoteActivity,"Adding User Information Success", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }
}