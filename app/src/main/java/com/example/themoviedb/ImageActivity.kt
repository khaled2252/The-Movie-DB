package com.example.themoviedb

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.media.MediaScannerConnection
import android.graphics.Bitmap
import java.nio.file.Files.delete
import java.nio.file.Files.exists
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val photoPath= this.openFileInput("profile_picture")
        val bitmap = BitmapFactory.decodeStream(photoPath)
        findViewById<ImageView>(R.id.iv_saveToGallery).setImageBitmap(bitmap)
        findViewById<Button>(R.id.btn_saveToGallery).setOnClickListener {
//            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image" , "No description available")
            saveImage(bitmap)
        }
    }
    private fun saveImage(finalBitmap: Bitmap) {

        val root = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString()
        val myDir = File(root)
        myDir.mkdirs()
        val generator = Random()

        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)

        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//             sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED,
//                 Uri.parse("file://"+ Environment.getExternalStorageDirectory())))
            out.flush()
            out.close()
            Toast.makeText(this,"Image saved to gallery !",Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

}
