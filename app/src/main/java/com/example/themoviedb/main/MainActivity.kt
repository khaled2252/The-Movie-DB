package com.example.themoviedb.main

import android.content.Intent
import android.graphics.Bitmap
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

    private var searchFlag: Boolean = false
    private var searchedWord: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = MainController(this)

        mRecyclerView = this.rv_popular_popular!!
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mRecyclerView.adapter = PopularPeopleAdapter(controller.resultList)
            mRecyclerView.addOnScrollListener(RecyclerViewListener())
            this.setItemViewCacheSize(100) //Cache  100 items instead of caching the visible items only which is the default
        }

        mSwipeRefreshLayout = this@MainActivity.srl
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED)
        mSwipeRefreshLayout.setOnRefreshListener {
            controller.clearData()

            if (searchFlag) {
                controller.loadSearchData(searchedWord)
            } else {
                controller.loadDefaultData()
            }

        }

        searchEditText = findViewById(R.id.searchEditText)
        val searchButton = findViewById<Button>(R.id.searchBtn)
        searchButton.setOnClickListener {
            searchedWord = searchEditText.text.toString()
            if (searchedWord.trim().isNotEmpty()) {
                searchFlag = true
                controller.clearData()
                controller.loadSearchData(searchedWord)
            }
        }

        val finishSearchBtn = findViewById<Button>(R.id.finishSearchBtn)
        finishSearchBtn.setOnClickListener {
            searchEditText.setText("")
            if (searchFlag) {
                controller.clearData()
                controller.loadDefaultData()
                searchFlag = false
            }
        }

        controller.onCreated()
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

    fun setImage(arr: Array<Any?>?){
        //FIXME Will only set images to items that are available on screen , if you scroll down while loading , images scrolled will stop loading
        val bitmap : Bitmap? = arr?.get(0) as Bitmap?
        if(bitmap!==null) {
            mRecyclerView.findViewWithTag<ImageView>(arr?.get(1))?.setImageBitmap(bitmap)
        }
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

    inner class PopularPeopleAdapter(private val list: List<Person?>) :
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
                val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.progress_bar, parent, false
                )
                ProgressViewHolder(v)
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
                    controller.itemViewOnClick(arrayOf(holder.itemView.context,(holder.itemView.findViewById<ImageView>(R.id.iv_profile).drawable as BitmapDrawable).bitmap),person)
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
                mProfilePicture?.setImageResource(R.drawable.no_image)

                if(!person.profile_path.isNullOrEmpty()) {
                    mProfilePicture?.tag = person.profile_path
                    controller.loadImage(person.profile_path)
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

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val pos = layoutManager.findLastCompletelyVisibleItemPosition()
                val numItems = recyclerView.adapter?.itemCount!! - 1

                if(controller.reachedEndOfScreen(pos,numItems))
                {
                    controller.scrollPage()

                    //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
                    controller.resultList.add(null)
                    mRecyclerView.adapter?.notifyItemRangeInserted(numItems, 1)

                    //Progress bar loads for 1 second then request new data to load
                    Handler().postDelayed({
                        controller.resultList.remove(null)
                        notifyItemRemovedFromRecyclerView(controller.resultList.size)

                        if (searchFlag) {
                            controller.loadSearchData(searchedWord)
                        } else {
                            controller.loadDefaultData()
                        }
                    }, 1000)
                    }
            }
        }
    }




