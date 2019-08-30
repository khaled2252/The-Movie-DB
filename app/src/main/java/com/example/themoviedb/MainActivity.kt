package com.example.themoviedb

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.themoviedb.pojos.Person
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.rv_popular_popular).apply { layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = CustomAdapter(resultList)}

        var url ="https://api.themoviedb.org/3/person/popular?api_key=3e68c56cf7097768305e38273efd342c"
        val asyncTask = AsyncTaskExample()
        asyncTask.execute(url)
    }


    private inner class AsyncTaskExample : AsyncTask<String, String, String?>() {
        var body : StringBuffer = StringBuffer()
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String? {
            try {
                val url = params[0]
                val urlConnection = URL(url).openConnection() as HttpURLConnection

                try {
                    val input = BufferedReader(
                        InputStreamReader(urlConnection.inputStream)
                    )

                    var inputLine: String?
                    do{
                        inputLine = input.readLine()
                        body.append(inputLine)
                    }
                    while ( inputLine !=null)
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

            val jsonObject = JSONObject(result)
            val jsonArray = jsonObject.getJSONArray("results") //List of objects (results)

            for (i in 0 until jsonArray.length()) {
                var person = Person()
                person.name=jsonArray.getJSONObject(i).getString("name")
                person.known_for_department=jsonArray.getJSONObject(i).getString("known_for_department")
                resultList.add(person)
            }
            findViewById<RecyclerView>(R.id.rv_popular_popular).apply { adapter?.notifyDataSetChanged() }

        }
    }
    companion object var resultList = ArrayList<Person>()

}
