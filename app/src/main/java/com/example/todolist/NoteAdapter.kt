package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

class
NoteAdapter(val ctx: Context, val noteList: ArrayList<NoteData>) : RecyclerView.Adapter<NoteAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView = view.findViewById(R.id.mNotebookTitle)
        var mLabel: TextView = view.findViewById(R.id.mNote)
        private var mMenus: ImageView = view.findViewById(R.id.mMenus)

        init {
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(view: View) {
            val position = noteList[adapterPosition]
            val popupMenus = PopupMenu(ctx, view)
            popupMenus.inflate(R.menu.note_item_context_menu)

            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.noteEditText -> {
                        val v = LayoutInflater.from(ctx).inflate(R.layout.add_item, null)
                        val name = v.findViewById<EditText>(R.id.userName)
                        val label = v.findViewById<EditText>(R.id.label)
                        AlertDialog.Builder(ctx)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                position.name = name.text.toString()
                                position.note_content = label.text.toString()
                                editNote(position, name.text.toString(), label.text.toString())
                                notifyDataSetChanged()
                                Toast.makeText(ctx, "User Information is Edited", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()

                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.noteDelete -> {
                        /**set delete*/
                        AlertDialog.Builder(ctx)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure delete this Information")
                            .setPositiveButton("Yes") { dialog, _ ->
                                noteList.removeAt(adapterPosition)
                                deleteNote(position)
                                notifyDataSetChanged()
                                Toast.makeText(ctx, "Deleted this Information", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else -> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.note_item, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = noteList[position]
        holder.name.text = newList.name
        holder.mLabel.text = newList.note_content
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    fun deleteNote(nt: NoteData){
        val client = OkHttpClient()

        val bod = RequestBody.create(
            MediaType.parse("application/json"),
            Json.encodeToString(CurrentUser.token)
        )
        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${CurrentNotebook.id}/notes/${nt.note_id}")
            .delete(bod)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code())
            }
        })
    }

    private fun editNote(nt: NoteData, name: String?, label: String?){
        val client = OkHttpClient()
        val bod = RequestBody.create(
            MediaType.parse("application/json"),
            """
            {
                "name": "$name",
                "note_content": "$label"
            }
            """.trimIndent()
        )
        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${CurrentNotebook.id}/notes/${nt.note_id}")
            .put(bod)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code())
            }
        })
    }

}