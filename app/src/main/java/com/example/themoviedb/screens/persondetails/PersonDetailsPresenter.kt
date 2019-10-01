package com.example.themoviedb.screens.persondetails

import com.example.themoviedb.base.BasePresenter
import com.example.themoviedb.models.Profile

class PersonDetailsPresenter(
    view: Contract.PersonDetailsView,
    repository: Contract.PersonDetailsRepository
) : BasePresenter<Contract.PersonDetailsView, Contract.PersonDetailsRepository>(view, repository) {
    override fun viewOnCreated() {
        view?.setUiFromIntent()
        loadProfiles {}
    }

    internal var resultList = ArrayList<Profile?>()

    private fun loadProfiles(isDataFetched: (Boolean) -> Unit) {
        repository.getProfile(view!!.getProfileId()) {
            isDataFetched(true)
            resultList.addAll(it!!)
            view?.notifyItemRangeChangedInRecyclerView(it.size)
            view?.showPersonInfo()
        }
    }

    fun itemViewOnClick(arr: Array<Any>) {
        repository.saveImage(arr)
        view?.navigateToImageActivity()
    }
}
