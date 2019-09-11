package com.example.themoviedb.persondetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PersonDetailsModel(private var personDetailsController: PersonDetailsController) {

    val API_KEY = "api_key=3e68c56cf7097768305e38273efd342c"
    val PERSON_DETAIL = "https://api.themoviedb.org/3/person/"
    val PERSON_IMAGES_ATTRIBUTE = "/images?"
    val PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w300/"

    inner class FetchData(asyncCallback: FetchDataCallback) : //takes reference from callback interface
        AsyncTask<String, String, String?>() {
        private var body: StringBuffer = StringBuffer()
        private var delegate = asyncCallback //Assigning callback interface through constructor

        override fun doInBackground(vararg params: String): String? {
            try {
                //params[0] is profile id
                val url = PERSON_DETAIL + params[0] + PERSON_IMAGES_ATTRIBUTE + API_KEY
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
            delegate.onProcessFinish(result!!)
        }
    }
     inner class FetchImages(fetchImagesCallback: FetchImagesCallback) : //takes reference from callback interface
     AsyncTask<String, Void, Bitmap?>() {
         private var delegate = fetchImagesCallback //Assigning callback interface through constructor

        override fun doInBackground(vararg params: String): Bitmap? {
            var bmp: Bitmap? = null
            try {
                val input = URL(PROFILE_IMAGE_PATH+params[0]).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            delegate.onProcessFinish(result)
        }
    }
}
interface FetchDataCallback {
    fun onProcessFinish(result: String)
}
interface FetchImagesCallback {
    fun onProcessFinish(result: Bitmap?)
}