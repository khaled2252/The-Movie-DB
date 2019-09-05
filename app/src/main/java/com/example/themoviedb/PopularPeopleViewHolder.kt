package com.example.themoviedb

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.themoviedb.R.drawable
import com.example.themoviedb.pojos.Person
import kotlinx.android.synthetic.main.row_layout.view.*
import java.net.URL

class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
    private var mNameView: TextView? = null
    private var mKnownForDepartementView: TextView? = null
    private var mPofilePicture: ImageView? = null

    init {
        mNameView = itemView.tv_name
        mKnownForDepartementView= itemView.tv_known_for_department
        mPofilePicture = itemView.iv_profile
    }

    fun bind(person : Person) {
        mNameView?.text = person.name
        mKnownForDepartementView?.text = person.known_for_department
        mPofilePicture?.setImageResource(drawable.no_image)
        DownloadImageTask(mPofilePicture!!).execute(Constants.PROFILE_IMAGE_PATH+person.profile_path)
    }
    private inner class DownloadImageTask(internal var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            var bmp: Bitmap? = null
            try {
                val input = URL(params[0]).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            if(result!=null)
                imageView.setImageBitmap(result)
        }
    }

}