package com.example.themoviedb.image

class ImageModel() {
    private lateinit var imageController: ImageController

    fun setImageController(imageController: ImageController) {
        this.imageController = imageController
    }
}