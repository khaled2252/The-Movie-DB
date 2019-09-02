package com.example.themoviedb

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(this, "Request is needed!", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        val mRecyclerView = findViewById<RecyclerView>(R.id.rv_popular_popular)
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PopularPeopleAdapter(resultList)
        }

        loadData(baseURL+pageAttr+currentPage.toString())

        val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
                val pos = layoutManager.findLastCompletelyVisibleItemPosition()
                val numItems = mRecyclerView.adapter?.itemCount!! -1

                if(pos >= numItems&&!isLoadingMore ) //Reached end of screen
                {
                    isLoadingMore=true

                    //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
                    resultList.add(null)
                    recyclerView.apply { mRecyclerView.adapter?.notifyDataSetChanged()}

                    Handler().postDelayed({
                        resultList.remove(null)
                        recyclerView.apply { recyclerView.adapter?.notifyItemRemoved(resultList.size) }
                        loadData(baseURL+pageAttr+currentPage.toString())
                    }, 1000)
                }
            }
        }
        mRecyclerView.addOnScrollListener(recyclerViewOnScrollListener)

        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.srl)
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            currentPage= 1

            val size = resultList.size
            if (size > 0) {
                for (i in 0 until size) {
                    resultList.removeAt(0)
                }
                mRecyclerView.adapter?.notifyItemRangeRemoved(0, size)
            }
            Handler().postDelayed({
                loadData(baseURL+pageAttr+currentPage.toString())
            }, 1000)

        }

    }

    fun loadData(url : String){
        val asyncTask = AsyncTaskExample()
        asyncTask.execute(url)
    }


    private inner class AsyncTaskExample(internal var body: StringBuffer = StringBuffer()) :
        AsyncTask<String, String, String?>() {

        override fun doInBackground(vararg params: String): String? {
            try {
                val url = params[0]
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
            currentPage++
            isLoadingMore=false

            val jsonArrayOfResults = JSONObject(result).getJSONArray("results") //List of objects (results)
            visibleThreshold=jsonArrayOfResults.length()

            //Map jsonArray to result list of pojos
            for (i in 0 until visibleThreshold) {
                val person = Person()
                person.name = jsonArrayOfResults.getJSONObject(i).getString("name")
                person.known_for_department = jsonArrayOfResults.getJSONObject(i).getString("known_for_department")
                person.profile_path = jsonArrayOfResults.getJSONObject(i).getString("profile_path")
                person.id = jsonArrayOfResults.getJSONObject(i).getString("id")
                //person.known_for = jsonArrayOfResults.getJSONObject(i).getJSONArray("known_for")
                resultList.add(person)
            }
            findViewById<RecyclerView>(R.id.rv_popular_popular).apply { adapter?.notifyDataSetChanged() }

            val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.srl)
            //Disable the refreshing icon when the result list is changed
            if (mSwipeRefreshLayout.isRefreshing) {
                mSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    companion object

    val baseURL = Constants.POPULAR_PEOPLE + Constants.API_KEY
    val pageAttr = Constants.PAGE_ATTRIBUTE
    var resultList = ArrayList<Person?>()
    var currentPage = 1
    var visibleThreshold = 0
    var isLoadingMore = false


}
