package com.example.themoviedb.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.R
import com.example.themoviedb.base.BaseActivity
import com.example.themoviedb.ui.persondetails.PersonDetailsActivity
import com.example.themoviedb.utils.models.Person
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainActivity : BaseActivity<MainPresenter>(), Contract.MainView {
    private var searchFlag: Boolean = false

    override val presenter = MainPresenter(this, MainRepository())

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        val mRecyclerView = this.rv_popular_popular!!
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter =
                PopularPeopleAdapter(presenter, presenter.resultList)
            addOnScrollListener(RecyclerViewListener())
            setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        val mSwipeRefreshLayout = this@MainActivity.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            val query = searchEditText.text.toString()
            presenter.layoutOnRefreshed(query)
        }

        val searchButton = findViewById<Button>(R.id.searchBtn)
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            presenter.searchOnClicked(query)
        }

        val finishSearchBtn = findViewById<Button>(R.id.finishSearchBtn)
        finishSearchBtn.setOnClickListener {
            presenter.finishSearchOnClicked()
        }
    }

    override fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int) {
        rv_popular_popular.adapter?.notifyItemRangeInserted(start, itemCount)
    }

    override fun notifyItemRemovedFromRecyclerView(index: Int) {
        rv_popular_popular.adapter?.notifyItemRemoved(index)
    }

    override fun notifyItemRangeChangedInRecyclerView(itemCount: Int) {
        rv_popular_popular.adapter?.notifyItemRangeChanged(
            rv_popular_popular.adapter!!.itemCount,
            itemCount
        )
    }

    override fun removeRefreshingIcon() {
        if (srl.isRefreshing) {
            srl.isRefreshing = false
        }
    }

    override fun navigateToPersonDetailsActivity(person: Person) {
        val intent = Intent(applicationContext, PersonDetailsActivity::class.java)
        intent.putExtra("profile_id", person.id.toString())
        intent.putExtra("person_name", person.name)
        intent.putExtra("known_for", person.knownFor as Serializable)
        intent.putExtra("known_for_department", person.knownForDepartment)
        intent.putExtra("popularity", person.popularity.toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    override fun instantiateNewAdapter() {
        rv_popular_popular.adapter =
            PopularPeopleAdapter(presenter, presenter.resultList)
    }

    override fun setSearchFlag(b: Boolean) {
        searchFlag = b
    }

    override fun getSearchFlag(): Boolean {
        return searchFlag
    }

    override fun clearSearchText() {
        searchEditText.setText("")
    }

    override fun getSearchText(): String {
        return searchEditText.text.toString()
    }

    override fun clearEditTextFocus() {
        searchEditText.clearFocus()
    }

    override fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    inner class RecyclerViewListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val pos =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val numItems = recyclerView.adapter?.itemCount!! - 1
            presenter.recyclerViewOnScrolled(pos, numItems)

        }
    }
}