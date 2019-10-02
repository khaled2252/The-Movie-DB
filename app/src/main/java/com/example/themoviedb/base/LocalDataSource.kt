package com.example.themoviedb.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class LocalDataSource {
    companion object {
        val Instance = LocalDataSource()
    }

    fun saveImageToStorage(arr: Array<Any>) {
        lateinit var fos: FileOutputStream
        val context: Context = arr[0] as Context
        val b: Bitmap = arr[1] as Bitmap

        try {
            fos = context.openFileOutput("profile_picture", Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos.close()
        }
    }

    fun saveImageToGallery(image: Any, imageSaved: (Boolean) -> Unit) {
        val bitmap = image as Bitmap
        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString()
        val myDir = File("$root/TheMovieDB")
        myDir.mkdirs()
        val generator = Random()

        var n = 10000
        n = generator.nextInt(n)
        val fName = "ImageActivity-$n.jpg"
        val file = File(myDir, fName)

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

    fun getSavedImageFromStorage(context: Any): Bitmap {
        val viewContext = context as Context
        val photoPath = viewContext.openFileInput("profile_picture")
        return BitmapFactory.decodeStream(photoPath)
    }
}