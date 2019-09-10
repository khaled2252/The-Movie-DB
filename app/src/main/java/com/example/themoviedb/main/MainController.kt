package com.example.themoviedb.main

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
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
import java.net.URL

class MainController(private val mainActivity: MainActivity) {
    private  var model : MainModel = MainModel(this)
    var isLoading = false
    var currentPage = 1
    var resultList = ArrayList<Person?>()
    var visibleThreshHold = 0


    fun loadData(page : Int, callback: (success: Boolean) -> Unit) {
        isLoading = true
        model.FetchData(object : AsyncCallback {

            override fun onProcessFinish(result: String) {
                //is called when data is fetched (in postExecute)
                if (result.isNotEmpty()) {
                    currentPage++
                    isLoading = false
                    //Todo Remove the progress bar when json is fetched and (make the RecyclerView Scrollable) not after all the images are fetched

                    //Remove loading progress bar if exists
                    if (resultList.size != 0 && resultList[resultList.size - 1] == null) {
                        resultList.remove(null)
                        mainActivity.notifyItemRemovedFromRecyclerView(resultList.size)
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

                    mainActivity.notifyItemRangeChangedInRecyclerView(visibleThreshHold)

                    //Disable the refreshing icon when the result list is changed
                    if (mainActivity.mSwipeRefreshLayout.isRefreshing) {
                        mainActivity.mSwipeRefreshLayout.isRefreshing = false
                    }

                    callback(true) //call back for loadData
                } else callback(false)
            }
        }).execute(page.toString())
    }
    fun loadData(searchedWord : String, page : Int, callback: (success: Boolean) -> Unit) {
        isLoading = true
        model.FetchData(object : AsyncCallback {

            override fun onProcessFinish(result: String) {
                //is called when data is fetched (in postExecute)
                if (result.isNotEmpty()) {
                    currentPage++
                    isLoading = false
                    //Todo Remove the progress bar when json is fetched and (make the RecyclerView Scrollable) not after all the images are fetched

                    //Remove loading progress bar if exists
                    if (resultList.size != 0 && resultList[resultList.size - 1] == null) {
                        resultList.remove(null)
                        mainActivity.notifyItemRemovedFromRecyclerView(resultList.size)
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

                    mainActivity.notifyItemRangeChangedInRecyclerView(visibleThreshHold)

                    //Disable the refreshing icon when the result list is changed
                    if (mainActivity.mSwipeRefreshLayout.isRefreshing) {
                        mainActivity.mSwipeRefreshLayout.isRefreshing = false
                    }

                    callback(true) //call back for loadData
                } else callback(false)
            }
        }).execute(page.toString(),searchedWord)
    }

    fun clearData() {
        currentPage = 1
        if (!isLoading) { //To avoid reloading when page is still loading more items (causes a bug to load the next page after reloading)
            val size = resultList.size
            if (size > 0) {
                for (i in 0 until size) {
                    resultList.removeAt(0)
                }
                mainActivity.notifyItemRangeRemovedInRecyclerView(size)
            }
        }
    }

    fun setRecyclerViewAdapter() {
        mainActivity.mRecyclerView.adapter =
            PopularPeopleAdapter(resultList)
    }

    fun setRecyclerViewOnScrollListener(){
        mainActivity.mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = mainActivity.mRecyclerView.layoutManager as LinearLayoutManager
                val pos = layoutManager.findLastCompletelyVisibleItemPosition()
                val numItems = mainActivity.mRecyclerView.adapter?.itemCount!! -1

                if(pos >= numItems&&!isLoading ) //Reached end of screen
                {
                    isLoading = true

                    //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
                    resultList.add(null)
                    mainActivity.notifyItemRangeInsertedInRecyclerView(numItems,1)

                    //Progress bar loads for 1 second then request new data to load
                    Handler().postDelayed({
                        resultList.remove(null)
                        mainActivity.notifyItemRemovedFromRecyclerView(resultList.size)

                        if (mainActivity.searchFlag == true){
                            mainActivity.requestSearch()
                        }
                        else{
                            mainActivity.requestPopularPeople()
                        }
                    }, 1000)
                }
            }
        })
    }

    inner class PopularPeopleAdapter(private val list: List<Person?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val VIEW_ITEM = 1
        val VIEW_PROG = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
            val vh: RecyclerView.ViewHolder
            val inflater = LayoutInflater.from(parent.context)

            if (viewType == VIEW_ITEM) {
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_layout, parent, false
                )
                vh = PopularPeopleViewHolder(inflater, parent)
            }
            else {
                val v = LayoutInflater.from(parent.context).inflate(
                    R.layout.progress_bar, parent, false
                )
                vh = ProgressViewHolder(v)
            }

            return vh
        }
        override fun getItemViewType(position: Int): Int {
            return if (list[position] != null) VIEW_ITEM else VIEW_PROG
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PopularPeopleViewHolder) {
                val person : Person? = list[position]
                holder.bind(person!!)
                holder.itemView.setOnClickListener {
                    //Navigate to Person Details activity
                    val intent = Intent(holder.itemView.context,PersonDetailsActivity::class.java)

                    val image = holder.itemView.findViewById<ImageView>(R.id.iv_profile)
                    val bitmap = (image.drawable as BitmapDrawable).bitmap
                    saveFile(holder.itemView.context,bitmap,"profile_picture")
                    intent.putExtra("profile_id",person.id)
                    intent.putExtra("person_name",person.name)
                    intent.putExtra("known_for",person.known_for)
                    intent.putExtra("known_for_department",person.known_for_department)
                    intent.putExtra("popularity",person.popularity)
                    holder.itemView.context.startActivity(intent)}
            }
            else {
                (holder as ProgressViewHolder).progressBar.isIndeterminate = true
            }
        }
        override fun getItemCount(): Int = list.size

        fun saveFile(context: Context, b: Bitmap, picName: String) {
            val fos: FileOutputStream
            try {
                fos = context.openFileOutput(picName, Context.MODE_PRIVATE)
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
       inner class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var progressBar: ProgressBar = v.findViewById(R.id.pb)

        }
    }

    inner class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
        private var mNameView: TextView? = null
        private var mKnownForDepartementView: TextView? = null
        private var mPofilePicture: ImageView? = null

        init {
            mNameView = itemView.findViewById(R.id.tv_name)
            mKnownForDepartementView= itemView.findViewById(R.id.tv_known_for_department)
            mPofilePicture = itemView.findViewById(R.id.iv_profile)
        }

        fun bind(person : Person) {
            mNameView?.text = person.name
            mKnownForDepartementView?.text = person.known_for_department
            mPofilePicture?.setImageResource(R.drawable.no_image)
            DownloadImageTask(mPofilePicture!!).execute(model.PROFILE_IMAGE_PATH+person.profile_path)
        }
        private inner class DownloadImageTask(internal var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

            override fun doInBackground(vararg params: String): Bitmap? {
                var bmp: Bitmap? = null
                try {
                    val input = URL(params[0]).openStream()
                    bmp = BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    Log.e("Error", e.message)
                    e.printStackTrace()
                }
                return bmp
            }

            override fun onPostExecute(result: Bitmap?) {
                if(result!=null)
                    imageView.setImageBitmap(result)
            }
        }

    }


}


