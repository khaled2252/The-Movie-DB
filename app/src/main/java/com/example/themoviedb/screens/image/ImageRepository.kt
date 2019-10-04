package com.example.themoviedb.screens.image

import com.example.themoviedb.base.BaseRepository

class ImageRepository : BaseRepository(), Contract.ImageRepository {
    override fun saveImageToGallery(imagePath: String, imageSaved: (Boolean) -> Unit) {
        localDataSource.saveImageToGallery(imagePath) {
            if (it)
                imageSaved(true)
            else
                imageSaved(false)
        }
    }

    override fun getSavedImagePath(): String {
        return localDataSource.getSavedImagePath()
    }
}