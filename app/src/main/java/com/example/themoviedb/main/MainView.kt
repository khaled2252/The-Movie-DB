package com.example.themoviedb.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.themoviedb.R
import com.example.themoviedb.persondetails.PersonDetailsView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainView : AppCompatActivity(), Contract.MainView {

    private lateinit var presenter: MainPresenter
    private lateinit var searchEditText: EditText
    private lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var mSwipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private var searchFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this, MainModel())

        mRecyclerView = this.rv_popular_popular!!
        mRecyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainView)
            adapter = PopularPeopleAdapter(presenter.resultList)
            addOnScrollListener(RecyclerViewListener())
            setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        mSwipeRefreshLayout = this@MainView.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            presenter.layoutOnRefreshed()
        }

        searchEditText = findViewById(R.id.searchEditText)
        val searchButton = findViewById<Button>(R.id.searchBtn)
        searchButton.setOnClickListener {
            presenter.searchOnClicked()
        }

        val finishSearchBtn = findViewById<Button>(R.id.finishSearchBtn)
        finishSearchBtn.setOnClickListener {
            presenter.finishSearchOnClicked()
        }

        presenter.viewOnCreated()
    }

    override fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeInserted(start, itemCount)
    }

    override fun notifyItemRemovedFromRecyclerView(index: Int) {
        this.mRecyclerView.adapter?.notifyItemRemoved(index)
    }

    override fun notifyItemRangeChangedInRecyclerView(itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeChanged(
            this.mRecyclerView.adapter!!.itemCount,
            itemCount
        )
    }

    override fun removeRefreshingIcon() {
        if (mSwipeRefreshLayout.isRefreshing) {
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    override fun navigateToPersonDetailsActivity(person: Person) {
        val intent = Intent(applicationContext, PersonDetailsView::class.java)
        intent.putExtra("profile_id", person.id)
        intent.putExtra("person_name", person.name)
        intent.putExtra("known_for", person.knownFor as Serializable)
        intent.putExtra("known_for_department", person.knownForDepartment)
        intent.putExtra("popularity", person.popularity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    override fun instantiateNewAdapter() {
        mRecyclerView.adapter = PopularPeopleAdapter(presenter.resultList)
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

    inner class PopularPeopleAdapter(private var list: ArrayList<com.example.themoviedb.main.Person?>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

        private val viewItem = 1
        private val viewProgress = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder
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

        override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
            if (holder is PopularPeopleViewHolder) {
                val person: Person? = list[position]
                holder.bind(person!!)
                holder.itemView.setOnClickListener {
                    val bitmap =
                        (holder.itemView.findViewById<ImageView>(R.id.iv_profile).drawable as BitmapDrawable).bitmap
                    presenter.itemViewOnClick(arrayOf(applicationContext, bitmap), person)
                }
            } else {
                (holder as ProgressViewHolder).progressBar.isIndeterminate = true
            }
        }

        override fun getItemCount(): Int = list.size

        inner class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
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
                mProfilePicture?.setImageBitmap(presenter.loadImage(person.profilePath)as Bitmap)
//                presenter.loadImage(person.profilePath) {
//                    mProgressBar.visibility = View.GONE
//                    if (it != null) {
//                        mProfilePicture?.setImageBitmap(it as Bitmap)
//                    } else
//                        mProfilePicture?.setImageResource(R.drawable.no_image)
//
//                }
            }
        }

        inner class ProgressViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
            var progressBar: ProgressBar = v.findViewById(R.id.pb)
        }
    }

    inner class RecyclerViewListener : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val pos =
                (recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val numItems = recyclerView.adapter?.itemCount!! - 1
            presenter.recyclerViewOnScrolled(pos, numItems)

        }
    }
}




