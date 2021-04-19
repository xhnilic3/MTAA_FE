package com.example.todolist

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

    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var noteList:ArrayList<NoteData>
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notebook)
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
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_note_item,null)
        /**set view*/
        val noteName = v.findViewById<EditText>(R.id.userName)
        val noteLabel = v.findViewById<EditText>(R.id.label)

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
                        "creator_id": ${CurrentUser.token.user.id},
                        "notebook_type": 1,
                        "notebook_name": "${names}",
                        "label": "${label}",
                        "notebook_color": "#000000",
                        "collaborator_id": null
                    }
                """.trimIndent()
            )

            //Fetching jwt
            val request = Request.Builder()
                .url("http://10.0.2.2:8000/notebooks/")
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
                        var newNote = Json.decodeFromString<NoteData>(response.body()?.string().toString())
                        noteList.add(newNote)
                        //Thread handling
                        this@NoteActivity.runOnUiThread(java.lang.Runnable {
                            noteAdapter.notifyDataSetChanged()
                        })

                    }

                    //TODO show message that login information was incorrect
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