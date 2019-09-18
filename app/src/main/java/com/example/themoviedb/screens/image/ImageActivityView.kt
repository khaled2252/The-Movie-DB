package com.example.themoviedb.screens.image

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.themoviedb.R
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivityView : AppCompatActivity(),
    Contract.ImageActivityView {

    private lateinit var presenter: ImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        presenter = ImagePresenter(
            this,
            ImageModel()
        )

        this.btn_saveToGallery.setOnClickListener {
            val builder = AlertDialog.Builder(this@ImageActivityView)
            builder.setTitle("Download image")
            builder.setMessage("Do you want to save image to gallery?")
            builder.setPositiveButton("YES") { _, _ ->
                presenter.yesOnClicked(applicationContext)
            }
            builder.setNegativeButton("No") { _, _ ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        presenter.viewOnCreated(this)
    }

    override fun showImageSavedToast() {
        Toast.makeText(this, "Image saved to gallery !", Toast.LENGTH_LONG).show()
    }

    override fun showErrorToast() {
        Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show()
    }

    override fun displayImage(bitmap: Bitmap) {
        this.iv_saveToGallery.setImageBitmap(bitmap)
    }

    override fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

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
    }

}
