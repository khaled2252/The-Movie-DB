package com.example.themoviedb

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.themoviedb.pojos.PersonImages
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


class PopularPersonAdapter(private val list: List<PersonImages?>): RecyclerView.Adapter<PopularPersonAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
                return list.size
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personImages : PersonImages? = list[position]
        holder.bind(personImages!!)

    }
   inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mImageView: ImageView = mView.findViewById(R.id.iv_image)
        fun bind(personImages: PersonImages) {
            mImageView.setImageResource(R.drawable.no_image)
            DownloadImageTask(mImageView).execute(Constants.PROFILE_IMAGE_PATH+personImages.filePath)
            mImageView.setOnClickListener {
                val intent = Intent(itemView.context,ImageActivity::class.java)
                val image = itemView.findViewById<ImageView>(R.id.iv_image)
                val bitmap = (image.drawable as BitmapDrawable).bitmap
                saveFile(itemView.context,bitmap,"profile_picture")
                itemView.context.startActivity(intent)}
            }
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
fun saveFile(context: Context, b: Bitmap, picName: String) {
    val fos: FileOutputStream
    try {
        fos = context.openFileOutput(picName, Context.MODE_PRIVATE)
        b.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (e: FileNotFoundException) {
        Log.d(ContentValues.TAG, "file not found")
        e.printStackTrace()
    } catch (e: IOException) {
        Log.d(ContentValues.TAG, "io exception")
        e.printStackTrace()
    } finally {
        //fos.close()
    }


}

