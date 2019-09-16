package com.example.themoviedb.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor

class MainModel : Contract.MainModel {

    private val executor: Executor? = AsyncTask.THREAD_POOL_EXECUTOR // To make parallel async task

    class FetchJson(fetchDataCallBack: FetchDataCallBack) :
        AsyncTask<String, String, String?>() { //takes reference from callback interface

        private var delegate = fetchDataCallBack //Assigning callback interface through constructor
        private var body: StringBuffer = StringBuffer()
        private lateinit var url: String

        override fun doInBackground(vararg params: String): String? {
            try {
                //params[0] is page number , params[1] is search queryz
                url = if (params.size == 1)
                    Companion.POPULAR_PEOPLE + Companion.API_KEY + Companion.PAGE_ATTRIBUTE + params[0]
                else
                    Companion.SEARCH_PERSON_NAME + params[1] + Companion.PAGE_ATTRIBUTE + params[0]

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
            delegate.onFetched(result)
        }
    }

    class FetchImage(FetchImageCallBack: FetchImageCallBack) : AsyncTask<String, Void, Bitmap?>() {
        private var delegate = FetchImageCallBack
        override fun doInBackground(vararg params: String): Bitmap? {
            var bmp: Bitmap? = null
            try {
                val input = URL(Companion.PROFILE_IMAGE_PATH + params[0]).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            delegate.onFetched(result)
        }
    }

    override fun fetchJson(currentPage : Int) {
        FetchJson(object : FetchDataCallBack {
            override fun onFetched(fetchedData: String?) {

            }
        }).executeOnExecutor(executor, currentPage.toString())
    }
    override fun fetchImage() {}

    override fun saveImage(arr: Array<Any>) {
        lateinit var fos: FileOutputStream
        val context: Context = arr[0] as Context
        val b: Bitmap = arr[1] as Bitmap

        try {
            fos = context.openFileOutput("profile_picture", Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos.close()
        }
    }

    companion object {
        const val API_KEY = "api_key=3e68c56cf7097768305e38273efd342c"
        const val PAGE_ATTRIBUTE = "&page="
        const val POPULAR_PEOPLE = "https://api.themoviedb.org/3/person/popular?"
        const val SEARCH_PERSON_NAME =
            "https://api.themoviedb.org/3/search/person?api_key=e6f20f39139b1f5a2be132cbaaa9ce43&query="
        const val PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w300/"
    }
}

interface FetchImageCallBack {
    fun onFetched(fetchedImage: Any?)//Callback method which is called at the object which implements the interface
}

interface FetchDataCallBack {
    fun onFetched(fetchedData: String?)
}



