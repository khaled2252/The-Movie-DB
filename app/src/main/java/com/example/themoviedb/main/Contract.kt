package com.example.themoviedb.main

import com.example.themoviedb.pojos.Person

interface Contract {
    interface MainModel {
        fun fetchJson(currentPage: Int, searchedWord: String?, fetchedData: (String?) -> Unit)
        fun fetchImage(path: String, fetchedImage: (Any?) -> Unit)
        fun saveImage(arr: Array<Any>)
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
        fun notifyItemRemovedFromRecyclerView(size: Int)
        fun notifyItemRangeInsertedFromRecyclerView(size: Int, i: Int)
        fun notifyItemRangeChangedInRecyclerView(visibleThreshHold: Int)
        fun navigateToPersonDetailsActivity(person: Person)
    }
}