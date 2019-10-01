package com.example.themoviedb.screens.persondetails

import com.example.themoviedb.base.BaseContract
import com.example.themoviedb.models.PersonProfilesResponse
import io.reactivex.Single

interface Contract {
    interface PersonDetailsRepository : BaseContract.BaseIRepository {
        fun getProfile(profileId: String): Single<PersonProfilesResponse>
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