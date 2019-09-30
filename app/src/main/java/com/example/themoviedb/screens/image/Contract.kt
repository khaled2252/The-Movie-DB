package com.example.themoviedb.screens.image

import android.graphics.Bitmap

interface Contract {
    interface ImageRepository {
        fun saveImageToGallery(image: Any, imageSaved: (Boolean) -> Unit)
        fun getSavedImage(context: Any): Bitmap
    }

    interface ImageActivityView {
        fun showImageSavedToast()
        fun showErrorToast()
        fun displayImage(bitmap: Bitmap)
        fun requestPermission()
    }
}