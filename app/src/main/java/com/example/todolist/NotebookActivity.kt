package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

class NotebookActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var addsBtn:FloatingActionButton
    private lateinit var recv:RecyclerView
    private lateinit var notebookList:ArrayList<NotebookData>
    private lateinit var notebookAdapter: NotebookAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        /**set List*/
        notebookList = ArrayList()
        /**set find Id*/
        addsBtn = findViewById(R.id.addingBtn)
        recv = findViewById(R.id.mRecycler)
        /**set Adapter*/
        notebookAdapter = NotebookAdapter(this,notebookList)
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = notebookAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addInfo() }




        //Getting stuff from database
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/user/${CurrentUser.token.user.id}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }
            override fun onResponse(call: Call, response: Response){
                val foo = Json.decodeFromString<List<NotebookData>>(response.body()?.string().toString())

                for (item in foo) notebookList.add(item)
                //Thread handling
                this@NotebookActivity.runOnUiThread(java.lang.Runnable {
                    notebookAdapter.notifyDataSetChanged()
                })
            }
        })
        //userAdapter.notifyDataSetChanged()
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_item,null)
        /**set view*/
        /*TODO dorobit tahanie notebookImage a notebookId z databazy*/
        val notebookImage = 0
        val notebookId = 0
        val notebookName = v.findViewById<EditText>(R.id.userName)
        val notebookLabel = v.findViewById<EditText>(R.id.label)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
            dialog,_->
            val names = notebookName.text.toString()
            val label = notebookLabel.text.toString()
            notebookList.add(NotebookData(notebookId, notebookImage, "", 1, names, label, "", "", 0))
            notebookAdapter.notifyDataSetChanged()
            Toast.makeText(this,"Adding User Information Success",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
            dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }
    /**ok now run this */

}