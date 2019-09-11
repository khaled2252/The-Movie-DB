package com.example.themoviedb.image

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.themoviedb.R
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivity : AppCompatActivity() {

    private lateinit var imageController: ImageController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imageController = ImageController(this)

        imageController.requestPermission()

        displayImage(imageController.bitmap)

        this.btn_saveToGallery.setOnClickListener {
            val builder = AlertDialog.Builder(this@ImageActivity)
            builder.setTitle("Download image")
            builder.setMessage("Do you want to save image to gallery?")
            builder.setPositiveButton("YES") { _, _ ->
                imageController.saveImage(imageController.bitmap)
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun showImageSavedToast(){
        Toast.makeText(this, "Image saved to gallery !", Toast.LENGTH_LONG).show()
    }
    fun showErrorToast(){
        Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show()
    }

    fun displayImage(bitmap: Bitmap){
        this.iv_saveToGallery.setImageBitmap(bitmap)
    }


}
