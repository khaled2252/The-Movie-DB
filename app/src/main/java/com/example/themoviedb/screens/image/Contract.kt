package com.example.themoviedb.screens.image

import android.graphics.Bitmap
import com.example.themoviedb.base.BaseContract

interface Contract {
    interface ImageRepository : BaseContract.BaseRepository {
        fun saveImageToGallery(image: Bitmap, imageSaved: (Boolean) -> Unit)
        fun getSavedImage(): Bitmap
    }

    interface ImageView : BaseContract.BaseView {

        fun showImageSavedToast()
        fun showErrorToast()
        fun displayImage(bitmap: Bitmap)
        fun requestPermission()
        fun getContext(): Any
    }
}