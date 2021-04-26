package com.example.todolist

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.ImageView
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


class NotebookActivity : AppCompatActivity(), ImageFetcher {
    private val client = OkHttpClient()
    private lateinit var addsBtn:FloatingActionButton
    private lateinit var recv:RecyclerView
    private lateinit var notebookList:ArrayList<NotebookData>
    private lateinit var notebookAdapter: NotebookAdapter
    private var notebookIconImageView: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook)
        /**set List*/
        notebookList = ArrayList()
        /**set find Id*/
        addsBtn = findViewById(R.id.addingBtn)
        recv = findViewById(R.id.mRecycler)
        /**set Adapter*/
        notebookAdapter = NotebookAdapter(this, notebookList)
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = notebookAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addInfo() }


        findViewById<FloatingActionButton>(R.id.ntbSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }



        //Getting stuff from database
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/user/${CurrentUser.token.user.id}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                val foo = Json.decodeFromString<List<NotebookData>>(
                    response.body()?.string().toString()
                )

                for (item in foo) notebookList.add(item)
                // println(item)//item.setBackgroundColor(Color.parseColor("#000000"))
                //Thread handling
                this@NotebookActivity.runOnUiThread {
                    notebookAdapter.notifyDataSetChanged()
                }
            }
        })
        //userAdapter.notifyDataSetChanged()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.notebook_item_context_menu, menu)
        return true
    }

    private fun uriToBitmap(uri: Uri):
        Bitmap{
            return MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }



    override fun onEditImageClick(imageView: ImageView) {
        super.onEditImageClick(imageView)

        notebookIconImageView = imageView

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra("return-data", true)
        intent.putExtra("crop","true")
        intent.putExtra("scale", "true")
        intent.putExtra("aspectX", "16")
        intent.putExtra("aspectY", "9")

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (notebookIconImageView != null) {
            println("kurvapica")}

        if (resultCode != RESULT_OK) {return;}

        if (requestCode == 1) {
            val extras: Uri? = data?.data
            if (extras != null) {
                println("mrkva")
                val bitmap = uriToBitmap(extras)
                notebookIconImageView?.setImageBitmap(bitmap)
            }
        }
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_item, null)
        /**set view*/
        val notebookName = v.findViewById<EditText>(R.id.userName)
        val notebookLabel = v.findViewById<EditText>(R.id.label)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){ dialog, _->
            val names = notebookName.text.toString()
            val label = notebookLabel.text.toString()
            ///----------------------------------------------------------------------------
            val client = OkHttpClient()

            val bod = RequestBody.create(
                MediaType.parse("application/json"), """
                    {
                        "creator_id": ${CurrentUser.token.user.id},
                        "notebook_type": 1,
                        "notebook_name": "$names",
                        "label": "$label",
                        "notebook_color": "#34eb8f",
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
                    if (response.code() == 201) {
                        //Assigning token to global class
                        val newNotebook = Json.decodeFromString<NotebookData>(
                            response.body()?.string().toString()
                        )
                        notebookList.add(newNotebook)
                        //Thread handling
                        this@NotebookActivity.runOnUiThread {
                            notebookAdapter.notifyDataSetChanged()
                        }

                    }


                }
            })
            Toast.makeText(
                this@NotebookActivity,
                "Adding User Information Success",
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()

        }
        addDialog.setNegativeButton("Cancel"){ dialog, _->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }
    /**ok now run this */

}