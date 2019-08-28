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


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnHttp).setOnClickListener {
            val asyncTask = AsyncTaskExample()
            var url = "https://api.themoviedb.org/3/discover/movie?api_key=3e68c56cf7097768305e38273efd342c"
            asyncTask.execute(url)
            Toast.makeText(applicationContext,"ResponsePojo code is : "+asyncTask.get()[0],Toast.LENGTH_SHORT).show()

            val jsonObject = Gson().fromJson(asyncTask.get()[1], ResponsePojo::class.java)
            var someList : Array<Result>? = jsonObject.getResults()
            someList!!.forEach { findViewById<TextView>(R.id.tv_body).append(it.toString())}
                        }
        }

    private inner class AsyncTaskExample : AsyncTask<String, String, Array<String>>() {
        var code = 0
        var body = ""
        override fun doInBackground(vararg strings: String): Array<String> {
            try {
                var client = OkHttpClient()

                val request = Request.Builder()
                    .url(strings[0])
                    .build()
                client.newCall(request).execute().use { response ->
                    body = response.body?.string()!!
                    ;code = response.code
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return arrayOf(code.toString(), body)

        }
    }
}
