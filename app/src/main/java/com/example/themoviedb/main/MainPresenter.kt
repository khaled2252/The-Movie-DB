package com.example.themoviedb.main


class MainPresenter(private val view: Contract.MainView, private val model: Contract.MainModel) {
    private var isLoading = false
    private var currentPage = 1
    internal var resultList = ArrayList<Person?>()

    private fun loadData(dataFetched: (Boolean) -> Unit) {
        isLoading = true
        if (view.getSearchFlag()) {
            model.enqueueCall(currentPage,view.getSearchText()){model.enqueueCall(currentPage,null){
                onDataFetched(it)
                dataFetched(true)
            }
            }
        } else {
            model.enqueueCall(currentPage,null){
                onDataFetched(it)
                dataFetched(true)
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







