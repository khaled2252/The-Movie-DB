package com.example.themoviedb.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.themoviedb.utils.ApplicationSingleton
import java.io.*
import java.util.*

class LocalDataSource {
    companion object {
        val Instance = LocalDataSource()
    }

    fun saveImageToStorage(imageByteArray: ByteArray) {
        lateinit var fos: FileOutputStream
        lateinit var image: Bitmap
        try {
            fos = ApplicationSingleton.instance.openFileOutput(
                "profile_picture",
                Context.MODE_PRIVATE
            )
            image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos.close()
        }
    }

    fun saveImageToGallery(imagePath: String, imageSaved: (Boolean) -> Unit) {
        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString()
        val myDir = File("$root/TheMovieDB")
        myDir.mkdirs()
        val generator = Random()

        var n = 10000
        n = generator.nextInt(n)
        val fName = "Image-$n.jpg"
        val file = File(myDir, fName)

        try {
            val out = FileOutputStream(file)
            val image = FileInputStream( File(imagePath))
            image.copyTo(out)
            out.flush()
            out.close()
            imageSaved(true)
        } catch (e: Exception) {
            e.printStackTrace()
            imageSaved(false)
        }
    }

    fun getSavedImagePath(): String {
        return ApplicationSingleton.instance.getFileStreamPath("profile_picture").absolutePath
    }
}