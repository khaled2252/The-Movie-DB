package com.example.themoviedb.main

import com.example.themoviedb.pojos.KnownFor
import com.example.themoviedb.pojos.Person
import org.json.JSONObject

class MainController(private val view: MainActivity) {
    private var model: MainModel = MainModel(this)
    private var visibleThreshHold = 0
    private var isLoading = false
    private var currentPage = 1

    internal var resultList = ArrayList<Person?>()


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

    fun loadImage(path : String?){
        MainModel.FetchImage().execute(path)
    }
    fun onImageFetched(arr: Array<Any?>?) {
        view.setImage(arr)
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

    fun reachedEndOfScreen (pos : Int, numItems :Int) : Boolean{
        return (pos >= numItems && !isLoading)
    }

    fun scrollPage(){
        isLoading = true
        currentPage++
    }

    fun onCreated() {
        loadDefaultData()
    }

    fun itemViewOnClick(arr: Array<Any>,person: Person) {
        model.saveImage(arr)
        view.navigateToPersonDetailsActivity(person)
    }


}







