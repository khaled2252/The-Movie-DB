package com.example.themoviedb.persondetails

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.themoviedb.R
import com.example.themoviedb.image.ImageActivity
import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.PersonImages
import kotlinx.android.synthetic.main.activity_person_details.*
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PersonDetailsController(private val detailsActivity: PersonDetailsActivity) {
    private var model : PersonDetailsModel = PersonDetailsModel(this)

    var resultList = ArrayList<PersonImages?>()
    private val profileId = detailsActivity.intent.getStringExtra("profile_id")
    private val personName = detailsActivity.intent.getStringExtra("person_name")
    private val popularity = detailsActivity.intent.getStringExtra("popularity")
    private val knownFor = detailsActivity.intent.getSerializableExtra("known_for") as ArrayList<KnownFor?>?
    private val knownForDepartment = detailsActivity.intent.getStringExtra("known_for_department")

    private val photoPath = detailsActivity.openFileInput("profile_picture")
    private val bitmap = BitmapFactory.decodeStream(photoPath)

    fun setUiFromIntent(){
        detailsActivity.iv_profileImage.setImageBitmap(bitmap)
        detailsActivity.tv_name.text = personName

        val knownForArrayList: ArrayList<String> = ArrayList()
        if (knownFor != null) {
            for (i in 0 until knownFor.size)
                knownForArrayList.add(knownFor[i]!!.original_title!!)
        } else knownForArrayList.add("No movies found")

        detailsActivity.tv_knownFor.text =
            StringBuilder("$personName is known for $knownForDepartment in $knownForArrayList with popularity score of $popularity")
    }

    fun loadData( callback: (success: Boolean) -> Unit){
        model.FetchData(object : FetchDataCallback {

            override fun onProcessFinish(result: String) {
                //is called when data is fetched (in postExecute)
                if (result.isNotEmpty()) {
                    val jsonArrayOfProfiles = JSONObject(result).getJSONArray("profiles")

                    for (i in 0 until jsonArrayOfProfiles.length()) {
                        val personImages = PersonImages()
                        personImages.personId = JSONObject(result).getString("id")
                        personImages.filePath = jsonArrayOfProfiles.getJSONObject(i).getString("file_path")
                        resultList.add(personImages)
                    }
                    callback(true) //call back for loadSearchData
                } else callback(false)
            }
        }).execute(profileId)
    }
    fun setRecyclerViewAdapter() {
        detailsActivity.mRecyclerView.adapter =
           PopularPersonAdapter(resultList)
    }

    inner  class PopularPersonAdapter(private val list: List<PersonImages?>): RecyclerView.Adapter<PopularPersonAdapter.ViewHolder>() {
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
        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            private val mImageView: ImageView = mView.findViewById(R.id.iv_image)
            fun bind(personImages: PersonImages) {
                mImageView.setImageResource(R.drawable.no_image)

                model.FetchImages(object : FetchImagesCallback {

                    override fun onProcessFinish(result: Bitmap?) {
                        if (result!=null) {
                            mImageView.setImageBitmap(result)
                        }
                    }
                }).execute(personImages.filePath)

                mImageView.setOnClickListener {
                    val intent = Intent(itemView.context, ImageActivity::class.java)
                    val image = itemView.findViewById<ImageView>(R.id.iv_image)
                    val bitmap = (image.drawable as BitmapDrawable).bitmap
                    saveFile(itemView.context,bitmap,"profile_picture")
                    itemView.context.startActivity(intent)}
            }
            private fun saveFile(context: Context, b: Bitmap, picName: String) {
                lateinit var fos: FileOutputStream
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
                    fos.close()
                }


            }

        }

    }
}