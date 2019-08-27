package com.example.httpurlconnection

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.httpurlconnection.Pojos.Movie
import com.google.gson.Gson
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnHttp).setOnClickListener {
            val asyncTask = AsyncTaskExample()
            var url = "https://api.themoviedb.org/3/movie/550?api_key=3e68c56cf7097768305e38273efd342c"
            asyncTask.execute(url)
            Toast.makeText(applicationContext,"Response code is : "+asyncTask.get()[0],Toast.LENGTH_SHORT).show()
            Log.i("response_code",asyncTask.get()[1])
            findViewById<TextView>(R.id.tv_body).text=asyncTask.get()[1]
            val jsonObject = Gson().fromJson(asyncTask.get()[1], Movie::class.java)
            Log.i("json_object",jsonObject.title)


        }
        }

    private inner class AsyncTaskExample : AsyncTask<String, String, Array<String>>() {
        var code = 0
        var body = ""
        override fun doInBackground(vararg strings: String): Array<String> {
            try {
                val url = URL(strings[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod="GET"
                try {
                    body = URL(strings[0]).readText()
                    code = urlConnection.responseCode

                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return arrayOf(code.toString(),body)
        }

    }
}
