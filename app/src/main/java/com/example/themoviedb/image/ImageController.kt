package com.example.themoviedb.image

class ImageController(val imageActivity: ImageActivity, val model: ImageModel) {

    init {
        model.setImageController(this)
    }
}