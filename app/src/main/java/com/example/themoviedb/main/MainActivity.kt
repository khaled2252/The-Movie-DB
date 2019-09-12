package com.example.themoviedb.main

import android.content.Context
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
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


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

        //Load first page in popular people
        controller.loadDefaultData()
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

    fun setImage(arr: Array<Any?>?){
        val bitmap : Bitmap? = arr?.get(0) as Bitmap?
        if(bitmap!==null) {
            var vieww : View = mRecyclerView.findViewWithTag<ImageView>(arr?.get(1))
            mRecyclerView.findViewWithTag<ImageView>(arr?.get(1)).setImageBitmap(bitmap)
        }
    }

    fun removeRefreshingIcon(){
        if (mSwipeRefreshLayout.isRefreshing) {
            mSwipeRefreshLayout.isRefreshing = false
        }
    }
    
    inner class PopularPeopleAdapter(private val list: List<Person?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val viewItem = 1
        private val viewProgress = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val vh: RecyclerView.ViewHolder
            val inflater = LayoutInflater.from(parent.context)

            vh = if (viewType == viewItem) {
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
            return vh
        }

        override fun getItemViewType(position: Int): Int {
            return if (list[position] != null) viewItem else viewProgress
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PopularPeopleViewHolder) {
                val person: Person? = list[position]
                holder.bind(person!!)
                holder.itemView.setOnClickListener {
                    //Navigate to Person Details activity
                    val intent = Intent(holder.itemView.context, PersonDetailsActivity::class.java)

                    val image = holder.itemView.findViewById<ImageView>(R.id.iv_profile)
                    val bitmap = (image.drawable as BitmapDrawable).bitmap
                    saveFile(holder.itemView.context, bitmap)
                    intent.putExtra("profile_id", person.id)
                    intent.putExtra("person_name", person.name)
                    intent.putExtra("known_for", person.known_for)
                    intent.putExtra("known_for_department", person.known_for_department)
                    intent.putExtra("popularity", person.popularity)
                    holder.itemView.context.startActivity(intent)
                }
            } else {
                (holder as ProgressViewHolder).progressBar.isIndeterminate = true
            }
        }

        override fun getItemCount(): Int = list.size

        private fun saveFile(context: Context, b: Bitmap) {
            lateinit var fos: FileOutputStream
            try {
                fos = context.openFileOutput( "profile_picture", Context.MODE_PRIVATE)
                b.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                fos.close()
            }
        }

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
                mProfilePicture?.tag = person.profile_path

                if(!person.profile_path.isNullOrEmpty()) {
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
                    notifyItemRangeInsertedInRecyclerView(numItems, 1)
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




