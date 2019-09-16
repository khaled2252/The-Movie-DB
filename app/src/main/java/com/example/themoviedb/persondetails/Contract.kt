package com.example.themoviedb.persondetails

interface Contract {
    interface PersonDetailsModel {
        fun fetchJson(profileId : String, fetchedData: (String?) -> Unit)
        fun fetchImage(path: String, fetchedImage: (Any?) -> Unit)
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