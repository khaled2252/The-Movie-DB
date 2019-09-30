package com.example.themoviedb.screens.main

import com.example.themoviedb.models.Person


class MainPresenter(private val view: Contract.MainView, private val repository: Contract.MainRepository) {
    private var isLoading = false
    var currentPage = 1

    internal var resultList = ArrayList<Person?>()

    private fun loadData(vararg query: String, dataLoaded: () -> Unit) {
        isLoading = true
        if (query.isNotEmpty())
            repository.fetchJson(currentPage, query[0]) {
                onDataFetched(it)
                dataLoaded()
            }
        else {
            repository.fetchJson(currentPage, null) {
                onDataFetched(it)
                dataLoaded()
            }
        }
    }

    private fun onDataFetched(it: ArrayList<Person>?) {
        isLoading = false
        resultList.addAll(it!!)
        view.notifyItemRangeChangedInRecyclerView(it.size)
    }


    private fun clearData() {
        currentPage = 1
        resultList.clear()
        view.instantiateNewAdapter() //To remove cached and un-recycled itemViews
    }

    private fun removeProgressBar() {
        resultList.remove(null)
        view.notifyItemRemovedFromRecyclerView(resultList.size)
    }

    fun addProgressBar() {
        //Adapter will check if the the object is null then it will add ProgressViewHolder instead of PopularPeopleViewHolder
        resultList.add(null)
        view.notifyItemRangeInsertedFromRecyclerView(resultList.size, 1)
    }

    fun viewOnCreated() {
        view.clearEditTextFocus()
        loadData {}
    }

    fun itemViewOnClick(arr: Array<Any>, person: Person) {
        repository.saveImage(arr)
        view.navigateToPersonDetailsActivity(person)
    }

    fun recyclerViewOnScrolled(pos: Int, numItems: Int) {
        if (reachedEndOfScreen(pos, numItems)) {
            isLoading = true
            currentPage++

            if (resultList.size != 0) //Avoid adding progress bar when the list is empty i.e when using search after clearing data
                addProgressBar()

            loadData {
                removeProgressBar()
            }
        }
    }

    fun reachedEndOfScreen(pos: Int, numItems: Int): Boolean {
        return pos >= numItems && !isLoading
    }

    fun layoutOnRefreshed(query: String) {
        clearData()
        if (query.trim().isNotEmpty()) {
            loadData(query) {
                view.removeRefreshingIcon()
            }
        } else
            loadData {
                view.removeRefreshingIcon()
            }
    }

    fun searchOnClicked(query: String) {
        if (query.trim().isNotEmpty()) {
            view.setSearchFlag(true)
            view.hideKeyBoard()
            view.clearEditTextFocus()

            clearData()
            loadData(query) {}
        }
    }

    fun finishSearchOnClicked() {
        view.clearSearchText()
        view.clearEditTextFocus()
        view.hideKeyBoard()
        if (view.getSearchFlag()) {
            view.setSearchFlag(false)
            clearData()
            loadData {}
        }
    }

}







