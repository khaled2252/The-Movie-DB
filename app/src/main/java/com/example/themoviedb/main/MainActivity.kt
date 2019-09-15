package com.example.themoviedb.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.themoviedb.R
import com.example.themoviedb.persondetails.PersonDetailsActivity
import com.example.themoviedb.pojos.Person
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var controller: MainController
    private lateinit var searchEditText: EditText
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    internal var searchFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = MainController(this)

        mRecyclerView = this.rv_popular_popular!!
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PopularPeopleAdapter(controller.resultList)
            addOnScrollListener(RecyclerViewListener())
            setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        mSwipeRefreshLayout = this@MainActivity.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            controller.layoutOnRefreshed()
        }

        searchEditText = findViewById(R.id.searchEditText)
        val searchButton = findViewById<Button>(R.id.searchBtn)
        searchButton.setOnClickListener {
            controller.searchOnClicked()
        }

        val finishSearchBtn = findViewById<Button>(R.id.finishSearchBtn)
        finishSearchBtn.setOnClickListener {
            controller.finishSearchOnClicked()
        }

        controller.viewOnCreated()
    }

    fun notifyItemRangeInsertedFromRecyclerView(start : Int ,itemCount : Int ){
        this.mRecyclerView.adapter?.notifyItemRangeInserted(start,itemCount)
    }

    fun notifyItemRemovedFromRecyclerView(index: Int) {
        this.mRecyclerView.adapter?.notifyItemRemoved(index)
    }

    fun notifyItemRangeChangedInRecyclerView(itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeChanged(this.mRecyclerView.adapter!!.itemCount,itemCount)
    }

    fun notifyItemRangeRemovedInRecyclerView(itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeRemoved(0, itemCount)
    }

    fun removeRefreshingIcon(){
        if (mSwipeRefreshLayout.isRefreshing) {
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    fun navigateToPersonDetailsActivity(person: Person) {
        val intent = Intent(applicationContext, PersonDetailsActivity::class.java)
        intent.putExtra("profile_id", person.id)
        intent.putExtra("person_name", person.name)
        intent.putExtra("known_for", person.known_for)
        intent.putExtra("known_for_department", person.known_for_department)
        intent.putExtra("popularity", person.popularity)
        applicationContext.startActivity(intent)
    }

    fun removeProgressBarAndLoadDataAfterDelay(delay: Long) {
        Handler().postDelayed({
            controller.resultList.remove(null)
            notifyItemRemovedFromRecyclerView(controller.resultList.size)

            if (searchFlag) {
                controller.loadSearchData(getSearchText())
            } else {
                controller.loadDefaultData()
            }
        }, delay)
    }

    fun clearSearchText() {
        searchEditText.setText("")
    }

    fun getSearchText() : String{
        return searchEditText.text.toString()
    }

    fun clearEditTextFocus() {
        searchEditText.clearFocus()
    }

    fun hideKeyBoard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    inner class PopularPeopleAdapter( private var list: List<Person?>) :
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
                    val bitmap = (holder.itemView.findViewById<ImageView>(R.id.iv_profile).drawable as BitmapDrawable).bitmap
                    controller.itemViewOnClick(arrayOf(applicationContext,bitmap),person)
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

            init {
                mNameView = itemView.findViewById(R.id.tv_name)
                mKnownForDepartmentView = itemView.findViewById(R.id.tv_known_for_department)
                mProfilePicture = itemView.findViewById(R.id.iv_profile)
            }

            fun bind(person: Person) {
                mNameView?.text = person.name
                mKnownForDepartmentView?.text = person.known_for_department
                controller.loadImage(person.profile_path){
                    if (it != null) {
                        mProfilePicture?.setImageBitmap(it)
                    }
                    else
                        mProfilePicture?.setImageResource(R.drawable.no_image)

                    }
            }
        }

        inner class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var progressBar: ProgressBar = v.findViewById(R.id.pb)
        }
    }

    inner class  RecyclerViewListener :RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val pos = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val numItems = recyclerView.adapter?.itemCount!! - 1
                controller.recyclerViewOnScrolled(pos,numItems)

            }
        }
    }




