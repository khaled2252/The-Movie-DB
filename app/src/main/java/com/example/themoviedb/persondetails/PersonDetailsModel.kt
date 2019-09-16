package com.example.themoviedb.persondetails

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class PersonDetailsModel : Contract.PersonDetailsModel {
    override fun fetchJson(
        profileId: String,
        fetchedData: (String?) -> Unit
    ) {
        FetchJson(object : FetchDataCallBack {
            override fun onFetched(fetchedData: String?) {
                fetchedData(fetchedData)
            }
        }).executeOnExecutor(
            AsyncTask.THREAD_POOL_EXECUTOR,
            profileId
        )

    }

    override fun fetchImage(path: String, fetchedImage: (Any?) -> Unit) {
        FetchImage(object : FetchImageCallBack {
            override fun onFetched(fetchedImage: Any?) {
                fetchedImage(fetchedImage)
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path)
    }

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

    class FetchJson(fetchDataCallBack: FetchDataCallBack) :
        AsyncTask<String, String, String?>() { //takes reference from callback interface

        private var delegate = fetchDataCallBack //Assigning callback interface through constructor
        private var body: StringBuffer = StringBuffer()

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
            delegate.onFetched(result)
        }
    }

    class FetchImage(FetchImageCallBack: FetchImageCallBack) : AsyncTask<String, Void, Bitmap?>() {
        private var delegate = FetchImageCallBack
        override fun doInBackground(vararg params: String): Bitmap? {
            var bmp: Bitmap? = null
            try {
                //params[0] is profile id
                val input = URL(PROFILE_IMAGE_PATH + params[0]).openStream()
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

    companion object {
        const val PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w300/"
        const val API_KEY = "api_key=3e68c56cf7097768305e38273efd342c"
        const val PERSON_DETAIL = "https://api.themoviedb.org/3/person/"
        const val PERSON_IMAGES_ATTRIBUTE = "/images?"
    }
}

interface FetchImageCallBack {
    fun onFetched(fetchedImage: Any?)
}

interface FetchDataCallBack {
    fun onFetched(fetchedData: String?)
}

