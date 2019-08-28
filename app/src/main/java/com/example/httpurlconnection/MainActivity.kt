package com.example.httpurlconnection

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.httpurlconnection.Pojos.ResponsePojo
import com.example.httpurlconnection.Pojos.Result
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var url = "https://api.themoviedb.org/3/discover/movie?api_key=3e68c56cf7097768305e38273efd342c"

        findViewById<Button>(R.id.btn_http_url_connection).setOnClickListener {
            val asyncTask = AsyncTaskExample()
            asyncTask.execute(arrayOf("0", url))
        }

        findViewById<Button>(R.id.btn_ok_http).setOnClickListener {
            val asyncTask = AsyncTaskExample()
            asyncTask.execute(arrayOf("1", url))
        }

    }


    private inner class AsyncTaskExample : AsyncTask<Array<String>, String, Array<String?>>() {
        var code: Int? = 0
        var body: String? = ""
        var type: Int? = 0
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<TextView>(R.id.tv_body).text = ""
            findViewById<TextView>(R.id.tv_status).text = ""

        }

        override fun doInBackground(vararg params: Array<String>?): Array<String?>? {
            try {
                val url = params[0]!![1]
                if (params[0]!![0].toInt() == 0) {
                    type = 0
                    val urlConnection = URL(url).openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"
                    try {
                        body = URL(url).readText()
                        code = urlConnection.responseCode

                    } finally {
                        urlConnection.disconnect()
                    }
                } else {
                    type = 1
                    var client = OkHttpClient()
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    client.newCall(request).execute().use { response ->
                        body = response.body?.string()
                        ;code = response.code
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return arrayOf(code.toString(), body, type.toString())
        }

        override fun onPostExecute(result: Array<String?>?) {
            super.onPostExecute(result)
            Toast.makeText(applicationContext, "Response code is : " + this.get()[0], Toast.LENGTH_SHORT).show()
            val jsonObject = Gson().fromJson(this.get()[1], ResponsePojo::class.java)
            var someList: Array<Result>? = jsonObject.getResults()
            someList!!.forEach { findViewById<TextView>(R.id.tv_body).append(it.toString()) }
            if (result!![2]!!.toInt() == 1) {
                findViewById<TextView>(R.id.tv_status).text = "Serialized object from Ok-HTTP"
            } else
                findViewById<TextView>(R.id.tv_status).text = "Serialized object from HTTP-URL-Connection"


        }
    }
}
