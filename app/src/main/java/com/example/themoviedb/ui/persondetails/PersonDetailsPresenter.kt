package com.example.themoviedb.ui.persondetails

import com.example.themoviedb.base.BasePresenter
import com.example.themoviedb.utils.models.Profile
import io.reactivex.functions.Consumer

class PersonDetailsPresenter(
    view: Contract.PersonDetailsView,
    repository: Contract.PersonDetailsRepository
) : BasePresenter<Contract.PersonDetailsView, Contract.PersonDetailsRepository>(view, repository) {
    override fun viewOnCreated() {
        view?.setUiFromIntent()
        loadProfiles {}
    }

    internal var resultList = ArrayList<Profile?>()

    private fun loadProfiles(dataLoaded: () -> Unit) {
        subscribe(repository.getProfile(view!!.getProfileId()), Consumer {
            resultList.addAll(it.profiles)
            view?.notifyItemRangeChangedInRecyclerView(it.profiles.size)
            view?.showPersonInfo()
            dataLoaded()
        })
    }

    fun itemViewOnClick(imageByteArray: ByteArray) {
        repository.saveImage(imageByteArray)
        view?.navigateToImageActivity()
    }
}