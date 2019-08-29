package com.example.httpurlconnection

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.httpurlconnection.Pojos.ResponsePojo
import com.example.httpurlconnection.Pojos.Result
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayoutManager.VERTICAL
        var url ="https://api.themoviedb.org/3/person/popular?api_key=3e68c56cf7097768305e38273efd342cc"
        val asyncTask = AsyncTaskExample()
        asyncTask.execute(url)



    }


    private inner class AsyncTaskExample : AsyncTask<String, String, String?>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String? {
            var body : String ? = null
            try {
                val url = params[0]
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                try {
                    body = URL(url).readText()
                } finally {
                    urlConnection.disconnect()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return body
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val jsonObject = JSONObject(this.get())
            val jsonArray = jsonObject.getJSONArray("results") //List of objects (results)

            val resultList = ArrayList<Result>()
            for (i in 0 until jsonArray.length()) {
                resultList.add(jsonArray.opt(i) as Result)
            }

            findViewById<RecyclerView>(R.id.rv_popular_popular).apply { layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = CustomAdapter(resultList)
            }

        }
    }
}
