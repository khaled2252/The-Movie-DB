package com.example.themoviedb.screens.image

import android.graphics.Bitmap
import com.example.themoviedb.base.BaseContract

interface Contract {
    interface ImageRepository : BaseContract.BaseIRepository {
        fun saveImageToGallery(image: Any, imageSaved: (Boolean) -> Unit)
        fun getSavedImage(context: Any): Bitmap
    }

    interface ImageActivityView : BaseContract.BaseIView {

        fun showImageSavedToast()
        fun showErrorToast()
        fun displayImage(bitmap: Bitmap)
        fun requestPermission()
        fun getContext(): Any
    }
}