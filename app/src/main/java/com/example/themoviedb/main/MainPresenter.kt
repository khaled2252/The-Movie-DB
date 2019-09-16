package com.example.themoviedb.main

import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.Person
import org.json.JSONObject

class MainPresenter(private val view: Contract.MainView, private val model: Contract.MainModel) {
    private var visibleThreshHold = 0
    private var isLoading = false
    private var currentPage = 1
    internal var resultList = ArrayList<Person?>()

    private fun loadDefaultData(isDataFetched: (Boolean) -> Unit) {
        model.fetchJson(currentPage, null) {
            isDataFetched(true)
            onDataFetched(it)
        }
    }

    private fun loadSearchData(searchedWord: String, isDataFetched: (Boolean) -> Unit) {
        model.fetchJson(currentPage, searchedWord) {
            isDataFetched(true)
            onDataFetched(it)
        }
    }

    private fun loadData(dataFetched: (Boolean) -> Unit) {
        isLoading = true
        if (view.getSearchFlag()) {
            loadSearchData(view.getSearchText()) {
                if (it) {
                    isLoading = false
                    dataFetched(true)
                }
            }
        } else {
            loadDefaultData {
                if (it) {
                    isLoading = false
                    dataFetched(true)
                }
            }
        }
    }

    private fun onDataFetched(result: String?) {
        if (!result.isNullOrEmpty()) {

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
        }
    }

    private fun clearData() {
        currentPage = 1
        resultList.clear()
        view.instantiateNewAdapter() //To remove cached and unrecycled itemViews
    }

    private fun removeProgressBar() {
        resultList.remove(null)
        view.notifyItemRemovedFromRecyclerView(resultList.size)
    }

    private fun addProgressBar() {
        //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
        resultList.add(null)
        view.notifyItemRangeInsertedFromRecyclerView(resultList.size, 1)
    }

    fun loadImage(
        path: String?,
        bitmap: (Any?) -> Unit  //High order function (Callback) which takes a bitmap (casted in view)
    ) {
        model.fetchImage(path!!) {
            bitmap(it) //Calls the high order function and gives it a bitmap when image is fetched
        }
    }

    fun viewOnCreated() {
        view.clearEditTextFocus()
        loadData {
            isLoading = false
        }
    }

    fun itemViewOnClick(arr: Array<Any>, person: Person) {
        model.saveImage(arr)
        view.navigateToPersonDetailsActivity(person)
    }

    fun recyclerViewOnScrolled(pos: Int, numItems: Int) {
        if (pos >= numItems && !isLoading) //Reached end of screen
        {
            isLoading = true
            currentPage++

            if (resultList.size != 0) //Avoid adding progress bar when the list is empty i.e when using search after clearing data
                addProgressBar()

            loadData {
                isLoading = false
                removeProgressBar()
            }
        }
    }

    fun layoutOnRefreshed() {
        clearData()
        loadData {
            view.removeRefreshingIcon()
        }
    }

    fun searchOnClicked() {
        if (view.getSearchText().trim().isNotEmpty()) {
            view.setSearchFlag(true)
            view.hideKeyBoard()
            view.clearEditTextFocus()

            clearData()
            loadData {
                isLoading = false
            }

        }
    }

    fun finishSearchOnClicked() {
        view.clearSearchText()
        view.clearEditTextFocus()
        view.hideKeyBoard()
        if (view.getSearchFlag()) {
            view.setSearchFlag(false)
            clearData()
            loadData {
                isLoading = false
            }
        }
    }

}







