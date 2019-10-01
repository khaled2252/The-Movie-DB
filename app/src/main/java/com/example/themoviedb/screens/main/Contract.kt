package com.example.themoviedb.screens.main

import com.example.themoviedb.base.BaseContract
import com.example.themoviedb.models.Person
import com.example.themoviedb.models.PopularPeopleResponse
import io.reactivex.Single

interface Contract {
    interface MainRepository : BaseContract.BaseIRepository {
        fun fetchJson(
            currentPage: Int,
            searchedWord: String?
        ): Single<PopularPeopleResponse>

        fun saveImage(arr: Array<Any>)
    }

    interface MainView : BaseContract.BaseIView {
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