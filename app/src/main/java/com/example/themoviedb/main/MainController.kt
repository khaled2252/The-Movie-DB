package com.example.themoviedb.main

import android.graphics.Bitmap
import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.Person
import org.json.JSONObject

class MainController(private val view: MainActivity) {
    private var model: MainModel = MainModel(this)
    private var visibleThreshHold = 0
    private var isLoading = false
    private var currentPage = 1

    internal var resultList = ArrayList<Person?>()

    private fun clearData() {
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
            //TODO Remove the progress bar when json is fetched and (make the RecyclerView Scrollable) not after all the images are fetched

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
            view.removeRefreshingIcon() //If is refreshing

        }
    }

    fun loadImage(path : String?,bitmap: (Bitmap?) -> Unit ){ //High order function (Callback) which takes a bitmap
        MainModel.FetchImage(object : FetchImageCallBack{
            override fun onFetched(fetchedImage: Bitmap?) {
                bitmap(fetchedImage) //Calls the high order function and gives it a bitmap when image is fetched
            }
        }).execute(path)
    }

    fun viewOnCreated() {
        view.clearEditTextFocus()
        loadDefaultData()
    }

    fun itemViewOnClick(arr: Array<Any>,person: Person) {
        model.saveImage(arr)
        view.navigateToPersonDetailsActivity(person)
    }

    fun recyclerViewOnScrolled(pos : Int , numItems : Int) {
        if(pos >= numItems && !isLoading) //Reached end of screen
        {
            isLoading = true
            currentPage++

            //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
            resultList.add(null)
            view.notifyItemRangeInsertedFromRecyclerView(numItems,1)

            view.removeProgressBarAndLoadDataAfterDelay(1000)
        }
    }

    fun layoutOnRefreshed() {
        clearData()

        if (view.searchFlag) {
            loadSearchData(view.getSearchText())
        } else {
            loadDefaultData()
        }
    }

    fun searchOnClicked() {
        if (view.getSearchText().trim().isNotEmpty()) {
            view.searchFlag = true
            view.hideKeyBoard()
            view.clearEditTextFocus()

            clearData()
            loadSearchData(view.getSearchText())

        }
    }

    fun finishSearchOnClicked() {
        view.clearSearchText()
        view.clearEditTextFocus()
        view.hideKeyBoard()
        if (view.searchFlag) {
            clearData()
            loadDefaultData()
            view.searchFlag = false
        }
    }

}







