package com.example.themoviedb.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.themoviedb.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var controller: MainController

    internal lateinit var mRecyclerView: RecyclerView
    internal lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    internal lateinit var searchEditText: EditText

    internal var searchFlag: Boolean = false
    internal var  searchedWord: String = ""
    internal var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = MainController(this)

        mRecyclerView = this.rv_popular_popular!!
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            controller.setRecyclerViewAdapter()
            controller.setRecyclerViewOnScrollListener()
            this.setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        mSwipeRefreshLayout = this@MainActivity.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            controller.clearData()

            if (searchFlag == true) {
                controller.fetchData(searchedWord)
            } else {
                controller.fetchData()
            }

        }

        searchEditText = findViewById<EditText>(R.id.searchEditText)
        var searchButton = findViewById<Button>(R.id.searchBtn)
        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                searchedWord = searchEditText.text.toString()
                if (searchedWord.trim().isEmpty()) {

                } else {
                    searchFlag = true
                    controller.clearData()
                    controller.fetchData(searchedWord)
                }
            }
        })

        var finishSearchBtn = findViewById<Button>(R.id.finishSearchBtn)
        finishSearchBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                searchEditText.setText("")
                if (searchFlag == true) {
                    controller.clearData()
                    controller.fetchData()
                    searchFlag = false
                } else {

                }
            }
        })

        //Load first page in popular people
        controller.fetchData()
    }

    fun notifyItemRemovedFromRecyclerView(index: Int) {
        this.mRecyclerView.adapter?.notifyItemRemoved(index)
    }

    fun notifyItemRangeChangedInRecyclerView(itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeChanged(this.mRecyclerView.adapter!!.itemCount,itemCount)
    }

    fun notifyItemRangeInsertedInRecyclerView(start: Int, itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeInserted(start, itemCount)
    }

    fun notifyItemRangeRemovedInRecyclerView(itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeRemoved(0, itemCount)
    }
}


