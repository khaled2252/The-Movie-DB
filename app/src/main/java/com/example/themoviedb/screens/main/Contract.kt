package com.example.themoviedb.screens.main

import com.example.themoviedb.network.Person

interface Contract {
    interface MainModel {
        fun fetchJson(
            currentPage: Int,
            searchedWord: String?,
            resultList: (ArrayList<Person>?) -> Unit
        )

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
        fun notifyItemRemovedFromRecyclerView(index: Int)
        fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int)
        fun notifyItemRangeChangedInRecyclerView(itemCount: Int)
        fun navigateToPersonDetailsActivity(person: Person)
    }
}