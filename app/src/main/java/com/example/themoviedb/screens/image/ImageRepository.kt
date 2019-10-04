package com.example.themoviedb.screens.image

import android.graphics.Bitmap
import com.example.themoviedb.base.BaseRepository

class ImageRepository : BaseRepository(), Contract.ImageRepository {
    override fun saveImageToGallery(image: Bitmap, imageSaved: (Boolean) -> Unit) {
        localDataSource.saveImageToGallery(image) {
            if (it)
                imageSaved(true)
            else
                imageSaved(false)
        }
    }

    override fun getSavedImage(): Bitmap {
        return localDataSource.getSavedImageFromStorage()
    }
}