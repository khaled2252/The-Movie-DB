package com.example.themoviedb.main

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.themoviedb.R
import com.example.themoviedb.persondetails.PersonDetailsActivity
import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.Person
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MainController(private val view: MainActivity) {
    private var model: MainModel = MainModel(this)
    private var visibleThreshHold = 0
    private var isLoading = false
    private var resultList = ArrayList<Person?>()
    private var currentPage = 1


    fun loadDefaultData() {
        isLoading = true
        MainModel.FetchJson().execute(currentPage.toString())
    }

    fun loadSearchData(searchedWord: String) {
        isLoading = true
        MainModel.FetchJson().execute(currentPage.toString(), searchedWord)
    }

    fun onDataFetched(result: String?) {
        if (!result.isNullOrEmpty()) {
            isLoading = false
            //Todo Remove the progress bar when json is fetched and (make the RecyclerView Scrollable) not after all the images are fetched

            //Remove loading progress bar if exists
            if (resultList.size != 0 && resultList[resultList.size - 1] == null) {
                resultList.remove(null)
                view.notifyItemRemovedFromRecyclerView(resultList.size)
            }

            val jsonArrayOfResults =
                JSONObject(result).getJSONArray("results") //jsonArray of objects (results)
            visibleThreshHold =
                jsonArrayOfResults.length() //Number of elements visible in one page

            //Map jsonArray to result list of pojos
            for (i in 0 until visibleThreshHold) {
                val person = Person()

                person.name = jsonArrayOfResults.getJSONObject(i).getString("name")
                person.known_for_department =
                    jsonArrayOfResults.getJSONObject(i).getString("known_for_department")
                person.profile_path =
                    jsonArrayOfResults.getJSONObject(i).getString("profile_path")
                person.id = jsonArrayOfResults.getJSONObject(i).getString("id")
                person.popularity =
                    jsonArrayOfResults.getJSONObject(i).getString("popularity")

                val jsonArrayOfKnownFor = jsonArrayOfResults.getJSONObject(i)
                    .getJSONArray("known_for") //jsonArray of objects (knownFor)
                if (jsonArrayOfKnownFor.length() != 0) {
                    val knownForArrayList = arrayListOf<KnownFor>()
                    for (j in 0 until jsonArrayOfKnownFor.length() - 1) {
                        val knownFor = KnownFor()
                        try {
                            knownFor.original_title =
                                jsonArrayOfKnownFor.getJSONObject(j)
                                    .getString("original_title")
                        } catch (e: Exception) {
                            knownFor.original_title =
                                jsonArrayOfKnownFor.getJSONObject(j)
                                    .getString("original_name")
                        } finally {
                            knownForArrayList.add(knownFor)
                        }

                    }
                    person.known_for = knownForArrayList
                }
                resultList.add(person)
            }
            view.notifyItemRangeChangedInRecyclerView(visibleThreshHold)

            //Disable the refreshing icon when the result list is changed
            if (view.mSwipeRefreshLayout.isRefreshing) {
                view.mSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    fun onImageFetched(array: Array<Any?>?) {
        val bitmap : Bitmap = array?.get(0) as Bitmap
        val imageView : ImageView = array[1] as ImageView
        imageView.setImageBitmap(bitmap)
    }

    fun setRecyclerViewAdapter() {
        view.mRecyclerView.adapter =
            PopularPeopleAdapter(resultList)
    }

    fun setRecyclerViewOnScrollListener() {
        view.mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.mRecyclerView.layoutManager as LinearLayoutManager
                val pos = layoutManager.findLastCompletelyVisibleItemPosition()
                val numItems = view.mRecyclerView.adapter?.itemCount!! - 1

                if (pos >= numItems && !isLoading) //Reached end of screen
                {
                    isLoading = true
                    currentPage++

                    //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
                    resultList.add(null)
                    view.notifyItemRangeInsertedInRecyclerView(numItems, 1)

                    //Progress bar loads for 1 second then request new data to load
                    Handler().postDelayed({
                        resultList.remove(null)
                        view.notifyItemRemovedFromRecyclerView(resultList.size)

                        if (view.searchFlag) {
                            loadSearchData(view.searchedWord)
                        } else {
                            loadDefaultData()
                        }
                    }, 1000)
                }
            }
        })
    }

    fun clearData() {
        currentPage = 1
        if (!isLoading) { //To avoid reloading when page is still loading more items (causes a bug to load the next page after reloading)
            val size = resultList.size
            if (size > 0) {
                for (i in 0 until size) {
                    resultList.removeAt(0)
                }
                view.notifyItemRangeRemovedInRecyclerView(size)
            }
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
            val fos: FileOutputStream
            try {
                fos = context.openFileOutput( "profile_picture", Context.MODE_PRIVATE)
                b.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: FileNotFoundException) {
                Log.d(ContentValues.TAG, "file not found")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.d(ContentValues.TAG, "io exception")
                e.printStackTrace()
            } finally {
                //fos.close()
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
                MainModel.FetchImage().execute(person.profile_path,mProfilePicture)
            }

        }

        inner class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var progressBar: ProgressBar = v.findViewById(R.id.pb)
        }
    }
}







