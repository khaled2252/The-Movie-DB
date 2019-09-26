package com.example.themoviedb.screens.main

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.R
import com.example.themoviedb.network.Person
import com.example.themoviedb.screens.persondetails.PersonDetailsView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainView : AppCompatActivity(){

    private lateinit var viewModel: MainViewModel
    private var popularPeopleList : ArrayList<Person?> = ArrayList()
    private var searchFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = MainViewModel(
            MainModel()
        )

        subscribeUi(viewModel)

        val mRecyclerView = this.rv_popular_popular!!
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainView)
            adapter = PopularPeopleAdapter(popularPeopleList)
            addOnScrollListener(RecyclerViewListener())
            setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        val mSwipeRefreshLayout = this@MainView.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            val query = searchEditText.text.toString()
            viewModel.layoutOnRefreshed(query)
        }

        val searchButton = findViewById<Button>(R.id.searchBtn)
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            viewModel.searchOnClicked(query)
        }

        val finishSearchBtn = findViewById<Button>(R.id.finishSearchBtn)
        finishSearchBtn.setOnClickListener {
            viewModel.finishSearchOnClicked()
        }

        viewModel.viewOnCreated()
    }
    private fun subscribeUi(viewModel: MainViewModel) {
        viewModel.resultList.observe(this, Observer {
            popularPeopleList.addAll(it)
            rv_popular_popular.adapter?.notifyItemRangeChanged(rv_popular_popular.adapter!!.itemCount,it.size)
        })

    }
     fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int) {
        rv_popular_popular.adapter?.notifyItemRangeInserted(start, itemCount)
    }

     fun notifyItemRemovedFromRecyclerView(index: Int) {
        rv_popular_popular.adapter?.notifyItemRemoved(index)
    }

     fun removeRefreshingIcon() {
        if (srl.isRefreshing) {
            srl.isRefreshing = false
        }
    }

     fun navigateToPersonDetailsActivity(person: Person) {
        val intent = Intent(applicationContext, PersonDetailsView::class.java)
        intent.putExtra("profile_id", person.id.toString())
        intent.putExtra("person_name", person.name)
        intent.putExtra("known_for", person.knownFor as Serializable)
        intent.putExtra("known_for_department", person.knownForDepartment)
        intent.putExtra("popularity", person.popularity.toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

     fun instantiateNewAdapter() {
        //rv_popular_popular.adapter = PopularPeopleAdapter(viewModel.resultList)
    }

     fun setSearchFlag(b: Boolean) {
        searchFlag = b
    }

     fun getSearchFlag(): Boolean {
        return searchFlag
    }

     fun clearSearchText() {
        searchEditText.setText("")
    }

     fun getSearchText(): String {
        return searchEditText.text.toString()
    }

     fun clearEditTextFocus() {
        searchEditText.clearFocus()
    }

     fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    inner class PopularPeopleAdapter(private var list: ArrayList<Person?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val viewItem = 1
        private val viewProgress = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val viewHolder: RecyclerView.ViewHolder
            val inflater = LayoutInflater.from(parent.context)

            viewHolder = if (viewType == viewItem) {
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_layout, parent, false
                )
                PopularPeopleViewHolder(inflater, parent)
            } else {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.progress_bar, parent, false
                )
                ProgressViewHolder(view)
            }
            return viewHolder
        }

        override fun getItemViewType(position: Int): Int {
            return if (list[position] != null) viewItem else viewProgress
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PopularPeopleViewHolder) {
                val person: Person? = list[position]
                holder.bind(person!!)
                holder.itemView.setOnClickListener {
                    val bitmap =
                        (holder.itemView.findViewById<ImageView>(R.id.iv_profile).drawable as? BitmapDrawable)?.bitmap
                    if (bitmap != null) { //To avoid clicking while bitmap is not loaded yet
                        viewModel.itemViewOnClick(arrayOf(applicationContext, bitmap), person)
                    }
                }
            } else {
                (holder as ProgressViewHolder).progressBar.isIndeterminate = true
            }
        }

        override fun getItemCount(): Int = list.size

        inner class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
            private var mNameView: TextView? = null
            private var mKnownForDepartmentView: TextView? = null
            private var mProfilePicture: ImageView? = null
            private var mProgressBar: ProgressBar

            init {
                mNameView = itemView.findViewById(R.id.tv_name)
                mKnownForDepartmentView = itemView.findViewById(R.id.tv_known_for_department)
                mProfilePicture = itemView.findViewById(R.id.iv_profile)
                mProgressBar = itemView.findViewById(R.id.progressBar)
            }

            fun bind(person: Person) {
                mNameView?.text = person.name
                mKnownForDepartmentView?.text = person.knownForDepartment
                Picasso.get()
                    .load("https://image.tmdb.org/t/p/w300/" + person.profilePath)
                    .into(mProfilePicture, object : Callback {
                        override fun onSuccess() {
                            mProgressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            mProfilePicture?.setImageBitmap(
                                BitmapFactory.decodeResource(
                                    applicationContext.resources,
                                    R.drawable.no_image
                                )
                            )
                        }
                    })
            }
        }

        inner class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var progressBar: ProgressBar = v.findViewById(R.id.pb)
        }
    }

    inner class RecyclerViewListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val pos =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val numItems = recyclerView.adapter?.itemCount!! - 1
            viewModel.recyclerViewOnScrolled(pos, numItems)

        }
    }
}




