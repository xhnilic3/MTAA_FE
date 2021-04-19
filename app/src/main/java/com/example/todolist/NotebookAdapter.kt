package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException

class NotebookAdapter(val ctx: Context, val userList: ArrayList<NotebookData>) : RecyclerView.Adapter<NotebookAdapter.UserViewHolder>() {


    inner class UserViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        var image: ImageView
        var name: TextView
        var mLabel: TextView
        var mMenus: ImageView

        init {
            image = view.findViewById(R.id.mImage)
            name = view.findViewById<TextView>(R.id.mTitle)
            mLabel = view.findViewById<TextView>(R.id.mSubTitle)
            mMenus = view.findViewById(R.id.mMenus)
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(view: View) {
            val position = userList[adapterPosition]
            val popupMenus = PopupMenu(ctx, view)
            popupMenus.inflate(R.menu.notebook_item_context_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editText -> {
                        val v = LayoutInflater.from(ctx).inflate(R.layout.add_item, null)
                        val name = v.findViewById<EditText>(R.id.userName)
                        val label = v.findViewById<EditText>(R.id.label)
                        AlertDialog.Builder(ctx)
                                .setView(v)
                                .setPositiveButton("Ok") { dialog, _ ->
                                    position.notebook_name = name.text.toString()
                                    position.label = label.text.toString()
                                    editNotebook(position, name.getText().toString(), label.getText().toString())
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
                    R.id.delete -> {
                        /**set delete*/
                        AlertDialog.Builder(ctx)
                                .setTitle("Delete")
                                .setIcon(R.drawable.ic_warning)
                                .setMessage("Are you sure delete this Information")
                                .setPositiveButton("Yes") { dialog, _ ->
                                    userList.removeAt(adapterPosition)
                                    deleteNotebook(position)
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
                    R.id.editImage ->{
                        editImage(image)

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
        val v = inflater.inflate(R.layout.notebook_item, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = userList[position]
        holder.name.text = newList.notebook_name
        holder.mLabel.text = newList.label
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun deleteNotebook(ntb: NotebookData){
        val client = OkHttpClient()

        val bod = RequestBody.create(
            MediaType.parse("application/json"),
                Json.encodeToString(CurrentUser.token)
        )
        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${ntb.notebook_id}")
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

    fun editNotebook(ntb: NotebookData, name: String?, label: String?){
        val client = OkHttpClient()
        val bod = RequestBody.create(
            MediaType.parse("application/json"),
            """
            {
              "notebook_name": "${name}",
              "label": "${label}",
              "notebook_color": "${ntb.notebook_color}",
              "collaborator_id": ${ntb.collaborator_id}
            }
            """.trimIndent()
        )
        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${ntb.notebook_id}")
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

    fun editImage(image: ImageView){
        //val bytes:Bitmap = BitmapFactory.decodeFile("C:\\Users\\peter\\Desktop\\school\\MTAA_FE\\app\\src\\main\\res\\drawable");
        val client = OkHttpClient()

        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/12/icon")
            .build()
        var foo:Bitmap? = null

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code())
                foo = BitmapFactory.decodeStream(response.body()?.byteStream())
            }
        })

        image.setImageBitmap(foo)

    }



}