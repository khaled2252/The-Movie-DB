package com.example.themoviedb

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.themoviedb.pojos.KnownFor
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

        val mRecyclerView = findViewById<RecyclerView>(R.id.rv_popular_popular)
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PopularPeopleAdapter(resultList)
            this.setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        loadData(baseURL+pageAttr+currentPage.toString()) //Load first page

        val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.srl)
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {

            isreloadingData=true
            currentPage= 1
            mRecyclerView.clearOnScrollListeners() //because scrollListener is called when list is empty ?

            val size = resultList.size
            if (size > 0) {
                for (i in 0 until size) {
                    resultList.removeAt(0)
                }
                mRecyclerView.adapter?.notifyItemRangeRemoved(0, size)
            }
                loadData(baseURL+pageAttr+currentPage.toString())
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

            //Loaded a page
            currentPage++
            isLoadingMore=false

            if(isreloadingData) {mRecyclerView.addOnScrollListener(recyclerViewOnScrollListener)
                isreloadingData=false}



            val jsonArrayOfResults = JSONObject(result).getJSONArray("results") //jsonArray of objects (results)
            visibleThreshold=jsonArrayOfResults.length() //Number of elements visible in one page

            //Map jsonArray to result list of pojos
            for (i in 0 until visibleThreshold) {
                val person = Person()

                person.name = jsonArrayOfResults.getJSONObject(i).getString("name")
                person.known_for_department = jsonArrayOfResults.getJSONObject(i).getString("known_for_department")
                person.profile_path = jsonArrayOfResults.getJSONObject(i).getString("profile_path")
                person.id = jsonArrayOfResults.getJSONObject(i).getString("id")
                person.popularity= jsonArrayOfResults.getJSONObject(i).getString("popularity")

                val jsonArrayOfKnownFor= jsonArrayOfResults.getJSONObject(i).getJSONArray("known_for") //jsonArray of objects (knownFor)
                if(jsonArrayOfKnownFor.length()!=0) {
                    var knownForArrayList = arrayListOf<KnownFor>()
                    for (j in 0 until jsonArrayOfKnownFor.length() - 1) {
                        var knownFor = KnownFor()
                        try {
                            knownFor.original_title =
                                jsonArrayOfKnownFor.getJSONObject(j).getString("original_title")
                        }
                        catch (e:Exception){
                            knownFor.original_title =
                                jsonArrayOfKnownFor.getJSONObject(j).getString("original_name")
                        }
                        finally {
                            knownForArrayList.add(knownFor)
                        }

                    }
                    person.known_for = knownForArrayList
                }
                resultList.add(person)
            }
            mRecyclerView.apply {
                mRecyclerView.adapter?.notifyItemRangeChanged(mRecyclerView?.adapter!!.itemCount,visibleThreshold)
            }
            val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.srl)
            //Disable the refreshing icon when the result list is changed
            if (mSwipeRefreshLayout.isRefreshing) {
                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        var mRecyclerView: RecyclerView = findViewById(R.id.rv_popular_popular)
        val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
                val pos = layoutManager.findLastCompletelyVisibleItemPosition()
                numItems = mRecyclerView.adapter?.itemCount!! -1

                if(pos >= numItems&&!isLoadingMore ) //Reached end of screen
                {
                    isLoadingMore=true

                    //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
                    resultList.add(null)
                    recyclerView.apply { mRecyclerView.adapter?.notifyItemRangeInserted(numItems,1)}

                    //Progress bar loads for 1 second then request new data to load
                    Handler().postDelayed({
                        resultList.remove(null)
                        recyclerView.apply { recyclerView.adapter?.notifyItemRemoved(resultList.size) }
                        loadData(baseURL+pageAttr+currentPage.toString())
                    }, 1000)
                }
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
    var numItems = 0
    var isreloadingData = false


}
