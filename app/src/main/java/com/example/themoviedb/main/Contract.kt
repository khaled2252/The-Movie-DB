package com.example.themoviedb.main

interface Contract {
    interface MainModel {
        fun fetchJson(currentPage: Int, searchedWord: String?, fetchedData: (String?) -> Unit)
        fun fetchImage(path: String, fetchedImage: (Any?) -> Unit)
        fun saveImage(arr: Array<Any>)
        fun enqueueCall(currentPage: Int,searchedWord: String?,resultList: (ArrayList<Person>?)->Unit)
    }

    interface MainView {
        fun setSearchFlag(b: Boolean)
        fun getSearchFlag(): Boolean
        fun getSearchText(): String
        fun clearEditTextFocus()
        fun clearSearchText()
        fun hideKeyBoard()
        fun removeRefreshingIcon()
        fun instantiateNewAdapter()
        fun notifyItemRemovedFromRecyclerView(index: Int)
        fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int)
        fun notifyItemRangeChangedInRecyclerView(itemCount: Int)
        fun navigateToPersonDetailsActivity(person: Person)
    }
}