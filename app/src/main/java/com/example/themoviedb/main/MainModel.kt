package com.example.themoviedb.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainModel(mainController: MainController) {

init {
    controller = mainController
}
     class FetchJson : AsyncTask<String, String, String?>() {

        private var body: StringBuffer = StringBuffer()
        private lateinit var url: String

        override fun doInBackground(vararg params: String): String? {
            try {
                //params[0] is page number , params[1] is search query
                if (params.size == 1)
                    url = Companion.POPULAR_PEOPLE + Companion.API_KEY + Companion.PAGE_ATTRIBUTE + params[0]
                else
                    url = Companion.SEARCH_PERSON_NAME + params[1] + Companion.PAGE_ATTRIBUTE + params[0]

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
            controller.onDataFetched(result)
        }
    }
     class FetchImage: AsyncTask<Any, Void, Array<Any?>>() {

        override fun doInBackground(vararg params: Any): Array<Any?>{
            var bmp: Bitmap? = null
            try {
                //params[0] is image url , params[1] is imageView from viewHolder to be sent to onImageFetched
                val input = URL(Companion.PROFILE_IMAGE_PATH + params[0]).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return arrayOf(bmp,params[1])
        }

        override fun onPostExecute(result: Array<Any?>?) {
            controller.onImageFetched(result)
        }
    }

    companion object {
        lateinit var controller: MainController

        const val API_KEY = "api_key=3e68c56cf7097768305e38273efd342c"
        const val PAGE_ATTRIBUTE = "&page="
        const val POPULAR_PEOPLE = "https://api.themoviedb.org/3/person/popular?"
        const val SEARCH_PERSON_NAME ="https://api.themoviedb.org/3/search/person?api_key=e6f20f39139b1f5a2be132cbaaa9ce43&query="
        const val PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w300/"
    }
}




