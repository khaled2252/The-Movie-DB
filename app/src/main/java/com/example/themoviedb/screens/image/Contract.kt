package com.example.themoviedb.screens.image

import android.graphics.Bitmap
import com.example.themoviedb.base.BaseContract

interface Contract {
    interface ImageRepository : BaseContract.BaseRepository {
        fun saveImageToGallery(image: Any, imageSaved: (Boolean) -> Unit)
        fun getSavedImage(context: Any): Bitmap
    }

    interface ImageActivityView : BaseContract.BaseView {

        fun showImageSavedToast()
        fun showErrorToast()
        fun displayImage(bitmap: Bitmap)
        fun requestPermission()
        fun getContext(): Any
    }
}