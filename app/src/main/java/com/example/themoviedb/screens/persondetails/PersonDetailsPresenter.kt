package com.example.themoviedb.screens.persondetails

import com.example.themoviedb.models.Profile

class PersonDetailsPresenter(
    private val view: Contract.PersonDetailsView,
    private val repository: Contract.PersonDetailsRepository
) {

    internal var resultList = ArrayList<Profile?>()

    private fun loadProfiles(isDataFetched: (Boolean) -> Unit) {
        repository.fetchJson(view.getProfileId()) {
            isDataFetched(true)
            resultList.addAll(it!!)
            view.notifyItemRangeChangedInRecyclerView(it.size)
            view.showPersonInfo()
        }
    }

    fun viewOnCreated() {
        view.setUiFromIntent()
        loadProfiles {}
    }

    fun itemViewOnClick(arr: Array<Any>) {
        repository.saveImage(arr)
        view.navigateToImageActivity()
    }
}
