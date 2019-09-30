package com.example.themoviedb.screens.image

class ImagePresenter(
    private val view: Contract.ImageActivityView,
    private val repository: Contract.ImageRepository
) {

    fun yesOnClicked(context: Any) {
        val bitmap = repository.getSavedImage(context)
        repository.saveImageToGallery(bitmap) {
            if (it)
                view.showImageSavedToast()
            else
                view.showErrorToast()
        }
    }

    fun viewOnCreated(context: Any) {
        view.requestPermission()
        view.displayImage(repository.getSavedImage(context))
    }
}