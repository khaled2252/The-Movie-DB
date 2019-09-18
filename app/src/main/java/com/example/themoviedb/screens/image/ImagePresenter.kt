package com.example.themoviedb.screens.image

class ImagePresenter(
    private val view: Contract.ImageActivityView,
    private val model: Contract.ImageModel
) {

    fun yesOnClicked(context: Any) {

        model.saveImageToGallery(model.getSavedImage(context)) {
            if (it)
                view.showImageSavedToast()
            else
                view.showErrorToast()
        }
    }

    fun viewOnCreated(context: Any) {
        view.requestPermission()
        view.displayImage(model.getSavedImage(context))
    }
}