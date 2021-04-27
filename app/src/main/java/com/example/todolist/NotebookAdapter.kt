package com.example.todolist

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.RecyclerView
import com.apandroid.colorwheel.ColorWheel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException


class NotebookAdapter(val ctx: Context, val notebookList: ArrayList<NotebookData>) : RecyclerView.Adapter<NotebookAdapter.UserViewHolder>() {


    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var image: ImageView = view.findViewById(R.id.mImage)
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
                                    position.notebook_color,
                                    position.collaborator_id
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
//                        val intent = Intent(Intent.ACTION_PICK)
//                        intent.type = "image/*"
//                        ctx.startActivity(intent)
//
                        if (ctx is ImageFetcher) {
                            ctx.onEditImageClick(image, position)
                        }
                        true
                    }


                    R.id.editColor -> {
                        val v = LayoutInflater.from(ctx).inflate(
                            R.layout.change_color_notebook,
                            null
                        )
                        val color = v.findViewById<ColorWheel>(R.id.notebookColor)

                        AlertDialog.Builder(ctx)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                position.notebook_color = "#${Integer.toHexString(color.rgb.red)}${
                                    Integer.toHexString(
                                        color.rgb.green
                                    )
                                }${Integer.toHexString(color.rgb.blue)}"
                                editNotebook(
                                    position,
                                    position.notebook_name,
                                    position.label,
                                    "#${Integer.toHexString(color.rgb.red)}${
                                        Integer.toHexString(
                                            color.rgb.green
                                        )
                                    }${Integer.toHexString(color.rgb.blue)}",
                                    position.collaborator_id
                                )
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
                    R.id.shareNotebook -> {
                        val v = LayoutInflater.from(ctx).inflate(R.layout.share_notebook, null)
                        val name = v.findViewById<EditText>(R.id.collabName)

                        AlertDialog.Builder(ctx)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                shareNotebook(position, name.text.toString())
                                notifyDataSetChanged()
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
        holder.itemView.setBackgroundColor(Color.parseColor(notebookList[position].notebook_color))

        val client = OkHttpClient()


        client.newCall(
            Request.Builder()
                .url("http://10.0.2.2:8000/notebooks/${notebookList[position].notebook_id}/icon")
                .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code())
                if(response.code() != 404) {
                    val img = response.body()?.bytes()
                    var options = BitmapFactory.Options()
                    var bitmap = BitmapFactory.decodeByteArray(img, 0, img!!.size, options)

                    (ctx as Activity).runOnUiThread {
                        holder.image.setImageBitmap(bitmap)
                    }
                }

            }
        })


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

        client.newCall(Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${ntb.notebook_id}")
            .delete(bod)
            .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.code())
            }
        })
    }

    fun editNotebook(ntb: NotebookData, name: String?, label: String?, col: String?, colab: Int?){
        val client = OkHttpClient()
        val bod = RequestBody.create(
            MediaType.parse("application/json"),
            """
            {
              "notebook_name": "$name",
              "label": "$label",
              "notebook_color": "$col",
              "collaborator_id": $colab
            }
            """.trimIndent()
        )
        //Fetching jwt
        client.newCall(Request.Builder()
            .url("http://10.0.2.2:8000/notebooks/${ntb.notebook_id}")
            .put(bod)
            .build()
        ).enqueue(object : Callback {
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

    fun shareNotebook(ntb: NotebookData, name: String?){
        val client = OkHttpClient()
        client.newCall(Request.Builder()
            .url("http://10.0.2.2:8000/users/user/?name=$name")
            .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Fail debug")
                throw e
            }

            override fun onResponse(call: Call, response: Response) {
                Looper.prepare()
                if(response.code() != 200){
                    Toast.makeText(
                        ctx,
                        "User with that name doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    editNotebook(
                        ntb,
                        ntb.notebook_name,
                        ntb.label,
                        ntb.notebook_color,
                        response.body()?.string()?.toInt()
                    )
                    Toast.makeText(
                        ctx,
                        "Shared successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}