package com.example.themoviedb.screens.persondetails

import com.example.themoviedb.base.BaseContract
import com.example.themoviedb.models.Profile

interface Contract {
    interface PersonDetailsRepository : BaseContract.BaseIRepository {
        fun getProfile(profileId: String, resultList: (ArrayList<Profile>?) -> Unit)
        fun saveImage(arr: Array<Any>)
    }

    interface PersonDetailsView : BaseContract.BaseIView {
        fun navigateToImageActivity()
        fun setUiFromIntent()
        fun getProfileId(): String
        fun notifyItemRemovedFromRecyclerView(index: Int)
        fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int)
        fun notifyItemRangeChangedInRecyclerView(itemCount: Int)
        fun showPersonInfo()
    }
}