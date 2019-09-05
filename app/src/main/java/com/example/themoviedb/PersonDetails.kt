package com.example.themoviedb

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.PersonImages
import kotlinx.android.synthetic.main.activity_person_details.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class PersonDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)
        val profileId = intent.getStringExtra("profile_id")
        val personName = intent.getStringExtra("person_name")
        val popularity = intent.getStringExtra("popularity")
        val knownFor = intent.getSerializableExtra("known_for") as ArrayList<KnownFor?>?

        val knownForArrayList: ArrayList<String> = ArrayList()
        if(knownFor!=null) {
            for (i in 0 until knownFor!!.size)
                knownForArrayList.add(knownFor[i]!!.original_title!!)
        }
        else knownForArrayList.add("No movies found")
        val knownForDepartment = intent.getStringExtra("known_for_department")

        val photoPath= this.openFileInput("profile_picture")
        val bitmap = BitmapFactory.decodeStream(photoPath)
        this.iv_profileImage.setImageBitmap(bitmap)

        this.tv_name.text = personName

        this.tv_knownFor.text = StringBuilder("$personName is known for $knownForDepartment in $knownForArrayList with popularity score of $popularity")

        val mRecyclerView = this.rv_pictures
        mRecyclerView.apply {
            layoutManager = GridLayoutManager(this@PersonDetails, 3)
            adapter = PopularPersonAdapter(resultList)
            mRecyclerView.addItemDecoration(RecyclerViewItemDecorator(10))
            this.setItemViewCacheSize(50)
        }
        val asyncTask = AsyncTaskExample()
        asyncTask.execute(Constants.PERSON_DETAIL+profileId+Constants.PERSON_IMAGES_ATTRIBUTE+Constants.API_KEY)
    }
    inner class AsyncTaskExample(private var body: StringBuffer = StringBuffer()) :
        AsyncTask<String, String, String?>() {

        override fun doInBackground(vararg params: String): String? {
            try {
                val url = params[0]
                val urlConnection = URL(url).openConnection() as HttpURLConnection

                try {
                    val input = BufferedReader(
                        InputStreamReader(urlConnection.inputStream)
                    )

                    var inputLine: String?
                    do {
                        inputLine = input.readLine()
                        body.append(inputLine)
                    } while (inputLine != null)
                    input.close()
                } finally {
                    urlConnection.disconnect()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return body.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val jsonArrayOfProfiles = JSONObject(result).getJSONArray("profiles")

            for (i in 0 until jsonArrayOfProfiles.length()) {
                var personImages = PersonImages()
                personImages.personId = JSONObject(result).getString("id")
                personImages.filePath = jsonArrayOfProfiles.getJSONObject(i).getString("file_path")
                resultList.add(personImages)
            }
            this@PersonDetails.rv_pictures.apply { adapter?.notifyDataSetChanged() }

        }
    }

    companion object

    var resultList = ArrayList<PersonImages?>()

}

//To remove spaces in grid view
class RecyclerViewItemDecorator(private val spaceInPixels: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = spaceInPixels
        outRect.right = spaceInPixels
        outRect.bottom = spaceInPixels

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = spaceInPixels
        } else {
            outRect.top = 0
        }
    }
}
