package com.example.themoviedb.screens.image

import com.example.themoviedb.base.BasePresenter

class ImagePresenter(
    view: Contract.ImageView,
    repository: Contract.ImageRepository
) : BasePresenter<Contract.ImageView, Contract.ImageRepository>(view, repository) {

    override fun viewOnCreated() {
        view?.requestPermission()
        view?.displayImage(repository.getSavedImage())
    }

    fun yesOnClicked() {
        val bitmap = repository.getSavedImage()
        repository.saveImageToGallery(bitmap) {
            if (it)
                view?.showImageSavedToast()
            else
                view?.showErrorToast()
        }
    }
}