package com.example.themoviedb

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.Person
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    var resultList = ArrayList<Person?>()
    var currentPage = 1
    var visibleThreshold = 0
    var isLoading = false
    var numItems = 0

    val baseURL = Constants.POPULAR_PEOPLE + Constants.API_KEY
    val pageAttr = Constants.PAGE_ATTRIBUTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mRecyclerView = this.rv_popular_popular
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = PopularPeopleAdapter(resultList)
            this.setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
            loadData(baseURL + pageAttr + currentPage.toString()) //Load first page
        }

        val mSwipeRefreshLayout = this.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            clearThenRequestData(baseURL + pageAttr + currentPage)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val mSearchItem = menu?.findItem(R.id.menu_search)
        val mSearchView = mSearchItem?.actionView as SearchView
        val mCloseButton = mSearchView.findViewById<View>(
            resources.getIdentifier(
                "android:id/search_close_btn",
                null,
                null
            )
        )

        mSearchView.queryHint = "Search by name..."

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query?.isNotEmpty()!!) {
                    //Make a request for search
                    clearThenRequestData(Constants.SEARCH_BY_PERSON + Constants.API_KEY + Constants.QUERY_ATTRIBUTE + query)
                } else {
                    currentPage = 1
                    clearThenRequestData(baseURL + pageAttr + currentPage)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        //To override what happens when x is clicked on in searchView
        mCloseButton.setOnClickListener {
            if (mSearchView.query.isNotEmpty()) {
                mSearchView.setQuery("", false)
                mSearchView.clearFocus()
                currentPage = 1
                clearThenRequestData(baseURL + pageAttr + currentPage)
            } else
                mSearchView.onActionViewCollapsed()

        }
        return super.onCreateOptionsMenu(menu)
    }

    fun loadData(url: String) {
        val asyncTask = AsyncTaskExample()
        asyncTask.execute(url)
    }

    fun clearThenRequestData(url: String) {
        val mRecyclerView = this.rv_popular_popular
        if (!isLoading) { //To avoid reloading when page is still loading more items (causes a bug to load the next page after reloading)
            mRecyclerView.clearOnScrollListeners() //because scrollListener is called when list is empty ?

            val size = resultList.size
            if (size > 0) {
                for (i in 0 until size) {
                    resultList.removeAt(0)
                }
                mRecyclerView.adapter?.notifyItemRangeRemoved(0, size)
            }
            loadData(url)
        }
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
            isLoading = false
            mRecyclerView.addOnScrollListener(recyclerViewOnScrollListener)


            val jsonArrayOfResults =
                JSONObject(result).getJSONArray("results") //jsonArray of objects (results)
            visibleThreshold = jsonArrayOfResults.length() //Number of elements visible in one page

            //Map jsonArray to result list of pojos
            for (i in 0 until visibleThreshold) {
                val person = Person()

                person.name = jsonArrayOfResults.getJSONObject(i).getString("name")
                person.known_for_department =
                    jsonArrayOfResults.getJSONObject(i).getString("known_for_department")
                person.profile_path = jsonArrayOfResults.getJSONObject(i).getString("profile_path")
                person.id = jsonArrayOfResults.getJSONObject(i).getString("id")
                person.popularity = jsonArrayOfResults.getJSONObject(i).getString("popularity")

                val jsonArrayOfKnownFor = jsonArrayOfResults.getJSONObject(i)
                    .getJSONArray("known_for") //jsonArray of objects (knownFor)
                if (jsonArrayOfKnownFor.length() != 0) {
                    var knownForArrayList = arrayListOf<KnownFor>()
                    for (j in 0 until jsonArrayOfKnownFor.length() - 1) {
                        var knownFor = KnownFor()
                        try {
                            knownFor.original_title =
                                jsonArrayOfKnownFor.getJSONObject(j).getString("original_title")
                        } catch (e: Exception) {
                            knownFor.original_title =
                                jsonArrayOfKnownFor.getJSONObject(j).getString("original_name")
                        } finally {
                            knownForArrayList.add(knownFor)
                        }

                    }
                    person.known_for = knownForArrayList
                }
                resultList.add(person)
            }
            mRecyclerView.apply {
                mRecyclerView.adapter?.notifyItemRangeChanged(
                    mRecyclerView?.adapter!!.itemCount,
                    visibleThreshold
                )
            }
            val mSwipeRefreshLayout = this@MainActivity.srl
            //Disable the refreshing icon when the result list is changed
            if (mSwipeRefreshLayout.isRefreshing) {
                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        var mRecyclerView: RecyclerView = this@MainActivity.rv_popular_popular
        val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
                val pos = layoutManager.findLastCompletelyVisibleItemPosition()
                numItems = mRecyclerView.adapter?.itemCount!! - 1

                if (pos >= numItems && !isLoading) //Reached end of screen
                {
                    isLoading = true

                    //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
                    resultList.add(null)
                    recyclerView.apply {
                        mRecyclerView.adapter?.notifyItemRangeInserted(
                            numItems,
                            1
                        )
                    }

                    //Progress bar loads for 1 second then request new data to load
                    Handler().postDelayed({
                        resultList.remove(null)
                        recyclerView.apply { recyclerView.adapter?.notifyItemRemoved(resultList.size) }
                        loadData(baseURL + pageAttr + currentPage.toString())
                    }, 1000)
                }
            }
        }
    }

}
