package com.example.themoviedb.screens.image

import com.example.themoviedb.base.BasePresenter

class ImagePresenter(
    view: Contract.ImageActivityView,
    repository: Contract.ImageRepository
) : BasePresenter<Contract.ImageActivityView, Contract.ImageRepository>(view, repository) {

    override fun viewOnCreated() {
        view?.requestPermission()
        view?.displayImage(repository.getSavedImage(view?.getContext()!!))
    }

    fun yesOnClicked(context: Any) {
        val bitmap = repository.getSavedImage(context)
        repository.saveImageToGallery(bitmap) {
            if (it)
                view?.showImageSavedToast()
            else
                view?.showErrorToast()
        }
    }
}