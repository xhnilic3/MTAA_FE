package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.RecyclerView
import com.apandroid.colorwheel.ColorWheel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import kotlin.collections.ArrayList

class NotebookAdapter(val ctx: Context, val notebookList: ArrayList<NotebookData>) : RecyclerView.Adapter<NotebookAdapter.UserViewHolder>() {


    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var image: ImageView = view.findViewById(R.id.mImage)
        var name: TextView = view.findViewById(R.id.mTitle)
        var mLabel: TextView = view.findViewById(R.id.mSubTitle)
        private var mMenus: ImageView = view.findViewById(R.id.mMenus)

        init {
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(view: View) {
            val position = notebookList[adapterPosition]
            val popupMenus = PopupMenu(ctx, view)
            popupMenus.inflate(R.menu.notebook_item_context_menu)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.open -> {
                        CurrentNotebook.id = position.notebook_id

                        val intent = Intent(ctx, NoteActivity::class.java)
                        ctx.startActivity(intent)

                        true
                    }
                    R.id.editText -> {
                        val v = LayoutInflater.from(ctx).inflate(R.layout.add_item, null)
                        val name = v.findViewById<EditText>(R.id.userName)
                        val label = v.findViewById<EditText>(R.id.label)
                        AlertDialog.Builder(ctx)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                position.notebook_name = name.text.toString()
                                position.label = label.text.toString()
                                editNotebook(
                                    position,
                                    name.text.toString(),
                                    label.text.toString(),
                                    position.notebook_color
                                )
                                notifyDataSetChanged()
                                Toast.makeText(
                                    ctx,
                                    "Notebook Information is Edited",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                notebookList.removeAt(adapterPosition)
                                deleteNotebook(position)
                                notifyDataSetChanged()
                                Toast.makeText(
                                    ctx,
                                    "Deleted this Information",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.editImage -> {
                        editImage(image)
                        true
                    }
                    R.id.editColor -> {
                        val v = LayoutInflater.from(ctx).inflate(R.layout.change_color_notebook, null)
                        val color = v.findViewById<ColorWheel>(R.id.notebookColor)

                        AlertDialog.Builder(ctx)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                position.notebook_color = "#${Integer.toHexString(color.rgb.red)}${Integer.toHexString(color.rgb.green)}${Integer.toHexString(color.rgb.blue)}"
                                editNotebook(position, position.notebook_name, position.label, "#${Integer.toHexString(color.rgb.red)}${Integer.toHexString(color.rgb.green)}${Integer.toHexString(color.rgb.blue)}")
                                notifyDataSetChanged()
                                Toast.makeText(
                                    ctx,
                                    "Notebook color changed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()

                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
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
        val v = inflater.inflate(R.layout.notebook_item, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = notebookList[position]
        holder.name.text = newList.notebook_name
        holder.mLabel.text = newList.label
        if (notebookList[position].notebook_color == "#000000") notebookList[position].notebook_color = "#777777"
        holder.itemView.setBackgroundColor(Color.parseColor(notebookList[position].notebook_color))

    }

    override fun getItemCount(): Int {
        return notebookList.size
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

    fun editNotebook(ntb: NotebookData, name: String?, label: String?, col: String?){
        val client = OkHttpClient()
        val bod = RequestBody.create(
            MediaType.parse("application/json"),
            """
            {
              "notebook_name": "$name",
              "label": "$label",
              "notebook_color": "$col",
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

//    TODO  edit image funguje, na stlacenie sa zobrazi image nahrany do db...
//          teraz treba poriesit to nahravanie a asi aj nieco, nech to tak ostane uchovane
//          mozno hned pri nacitavani tych notebookov by sa malo spravit to, ze ak image neni null,
//          vykona sa toto, ci? Rano sa mi k tomuto vyjadri, pls

    fun editImage(image: ImageView){

        val client = OkHttpClient()
        //Fetching jwt
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/41/icon")
            .build()
        var foo: ByteArray?

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                foo = response.body()?.bytes()

                MainScope().launch {
                    withContext(Dispatchers.Default){

                    }
                    image.setImageBitmap(BitmapFactory.decodeByteArray(foo, 0, foo!!.size))
                }

            }
        })




    }



}