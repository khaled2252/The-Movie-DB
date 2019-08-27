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
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var url1 = "https://api.themoviedb.org/3/movie/550?api_key=3e68c56cf7097768305e38273efd342c"
        var url2 = "https://api.themoviedb.org/3/movie/549?api_key=3e68c56cf7097768305e38273efd342c"
        findViewById<Button>(R.id.btnHttp).setOnClickListener {
            val asyncTask = AsyncTaskExample()
            asyncTask.execute(arrayOf("0",url1))
            Toast.makeText(applicationContext, "Response code is : " + asyncTask.get()[0], Toast.LENGTH_SHORT).show()
            Log.i("response_code", asyncTask.get()[1])
            findViewById<TextView>(R.id.tv_body).text = asyncTask.get()[1]
            val jsonObject = Gson().fromJson(asyncTask.get()[1], Movie::class.java)
            Log.i("json_object", jsonObject.title)


        }
        findViewById<Button>(R.id.btnOkHttp).setOnClickListener {

            val asyncTask = AsyncTaskExample()
            asyncTask.execute(arrayOf("1",url2))
            Toast.makeText(applicationContext, "Response code is : " + asyncTask.get()[0], Toast.LENGTH_SHORT).show()
            Log.i("response_code", asyncTask.get()[1])
            findViewById<TextView>(R.id.tv_body).text = asyncTask.get()[1]
            val jsonObject = Gson().fromJson(asyncTask.get()[1], Movie::class.java)
            Log.i("json_object", jsonObject.title)
        }
    }

    private inner class AsyncTaskExample : AsyncTask<Array<String>, String, Array<String?>>() {
        var code : Int? = 0
        var body : String? = ""
        override fun doInBackground(vararg params: Array<String>?): Array<String?>? {
        try {
                val url = params[0]!![1]
                if(params[0]!![0].toInt()==0)
                {
                    val urlConnection = URL(url).openConnection() as HttpURLConnection
                    urlConnection.requestMethod="GET"
                    try {
                        body = URL(url).readText()
                        code = urlConnection.responseCode

                    } finally {
                        urlConnection.disconnect()
                    }
                }
            else
                {            var client = OkHttpClient()

                    val request = Request.Builder()
                        .url(url)
                        .build()
                    client.newCall(request).execute().use { response -> body = response.body?.string()
                ;code = response.code
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return arrayOf(code.toString(),body)
        }

    }
}
