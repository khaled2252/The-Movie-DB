package com.example.themoviedb

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        val photoPath= this.openFileInput("profile_picture")
        val bitmap = BitmapFactory.decodeStream(photoPath)
        this.iv_saveToGallery.setImageBitmap(bitmap)
        this.btn_saveToGallery.setOnClickListener {
            val builder = AlertDialog.Builder(this@ImageActivity)
            builder.setTitle("Download image")
            builder.setMessage("Do you want to save image to gallery?")
            builder.setPositiveButton("YES"){ _, _ ->
               saveImage(bitmap)
                   //This line works as well but saves to Pictures directory
//                val n = Random().nextInt()
//                val fname = "TheMovieDB/Image$n.jpg"
//                val url =MediaStore.Images.Media.insertImage(contentResolver, bitmap,fname ,"No Description")
//                if(url!=null)
//                    Toast.makeText(this,"Image saved to gallery !",Toast.LENGTH_LONG).show()
            }
            builder.setNegativeButton("No"){ _, _ ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
    private fun saveImage(finalBitmap: Bitmap) {

        val root = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES
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
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
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
