package com.example.themoviedb

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.pojos.Person
import kotlinx.android.synthetic.main.progress_bar.view.*
import kotlinx.android.synthetic.main.row_layout.view.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class PopularPeopleAdapter(private val list: List<Person?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        val vh: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == VIEW_ITEM) {
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_layout, parent, false
            )
            vh = PopularPeopleViewHolder(inflater,parent)
        }
        else {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.progress_bar, parent, false
            )
            vh = ProgressViewHolder(v)
        }

        return vh
    }
    override fun getItemViewType(position: Int): Int {
        return if (list[position] != null) VIEW_ITEM else VIEW_PROG
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PopularPeopleViewHolder) {
        val person : Person? = list[position]
        holder.bind(person!!)
            holder.itemView.setOnClickListener {
                //Navigate to Person Details activity
                val intent = Intent(holder.itemView.context,PersonDetails::class.java)

                val image = holder.itemView.iv_profile
                val bitmap = (image.drawable as BitmapDrawable).bitmap
                saveFile(holder.itemView.context,bitmap,"profile_picture")
                intent.putExtra("profile_id",person.id)
                intent.putExtra("person_name",person.name)
                intent.putExtra("known_for",person.known_for)
                intent.putExtra("known_for_department",person.known_for_department)
                intent.putExtra("popularity",person.popularity)
                holder.itemView.context.startActivity(intent)}
        }
        else {
            (holder as ProgressViewHolder).progressBar.isIndeterminate = true
        }
    }
    override fun getItemCount(): Int = list.size

    fun saveFile(context: Context, b: Bitmap, picName: String) {
        val fos: FileOutputStream
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "file not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d(TAG, "io exception")
            e.printStackTrace()
        } finally {
            //fos.close()
        }
    }
    companion object {
        const val VIEW_ITEM = 1
        const val VIEW_PROG = 0
    }

}

class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var progressBar: ProgressBar = v.pb

}