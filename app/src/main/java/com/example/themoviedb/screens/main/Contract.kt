package com.example.themoviedb.screens.main

import com.example.themoviedb.base.BaseContract
import com.example.themoviedb.utils.models.Person
import com.example.themoviedb.utils.models.PopularPeopleResponse
import io.reactivex.Single

interface Contract {
    interface MainRepository : BaseContract.BaseRepository {
        fun getPopularPeople(
            currentPage: Int,
            searchedWord: String?
        ): Single<PopularPeopleResponse>

        fun saveImage(imageByteArray: ByteArray)
    }

    interface MainView : BaseContract.BaseView {
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