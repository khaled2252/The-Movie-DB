package com.example.themoviedb.persondetails

import com.example.themoviedb.Profile

class PersonDetailsPresenter(
    private val view: Contract.PersonDetailsView,
    private val model: Contract.PersonDetailsModel
) {

    internal var resultList = ArrayList<Profile?>()

    private fun loadProfiles(isDataFetched: (Boolean) -> Unit) {
        model.fetchJson(view.getProfileId()) {
            isDataFetched(true)
            resultList.addAll(it!!)
            view.notifyItemRangeChangedInRecyclerView(it.size)
        }
    }

    fun viewOnCreated() {
        view.setUiFromIntent()
        loadProfiles {}
    }

    fun itemViewOnClick(arr: Array<Any>) {
        model.saveImage(arr)
        view.navigateToImageActivity()
    }
}
