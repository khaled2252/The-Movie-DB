package com.example.themoviedb.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ImageModel : Contract.ImageModel {
    override fun saveImageToGallery(image: Any, imageSaved: (Boolean) -> Unit) {
        val bitmap = image as Bitmap
        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString()
        val myDir = File("$root/TheMovieDB")
        myDir.mkdirs()
        val generator = Random()

        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            imageSaved(true)
        } catch (e: Exception) {
            e.printStackTrace()
            imageSaved(false)
        }
    }

    override fun getSavedImage(context: Any): Bitmap {
        val viewContext = context as Context
        val photoPath = viewContext.openFileInput("profile_picture")
        return BitmapFactory.decodeStream(photoPath)
    }
}