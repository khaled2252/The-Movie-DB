package com.example.themoviedb.persondetails

import com.example.themoviedb.Profile

interface Contract {
    interface PersonDetailsModel {
        fun fetchJson(profileId : String, resultList: (ArrayList<Profile>?)->Unit)
        fun saveImage(arr: Array<Any>)
    }

    interface PersonDetailsView {
        fun navigateToImageActivity()
        fun setUiFromIntent()
        fun getProfileId(): String
        fun notifyItemRemovedFromRecyclerView(index: Int)
        fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int)
        fun notifyItemRangeChangedInRecyclerView(itemCount: Int)
    }
}