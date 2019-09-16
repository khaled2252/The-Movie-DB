package com.example.themoviedb.image

import android.graphics.Bitmap

interface Contract {
    interface ImageModel {
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