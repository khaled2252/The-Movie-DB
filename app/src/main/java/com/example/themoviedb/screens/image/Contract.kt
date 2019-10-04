package com.example.themoviedb.screens.image

import com.example.themoviedb.base.BaseContract

interface Contract {
    interface ImageRepository : BaseContract.BaseRepository {
        fun saveImageToGallery(imagePath: String, imageSaved: (Boolean) -> Unit)
        fun getSavedImagePath(): String
    }

    interface ImageView : BaseContract.BaseView {

        fun showImageSavedToast()
        fun showErrorToast()
        fun displayImage(imagePath: String)
        fun requestPermission()
        fun getContext(): Any
    }
}