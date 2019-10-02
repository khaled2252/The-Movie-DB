package com.example.themoviedb.screens.image

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.themoviedb.R
import com.example.themoviedb.base.BaseActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : BaseActivity<ImagePresenter>(),
    Contract.ImageView {

    override val presenter = ImagePresenter(this, ImageRepository())

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_image
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        this.btn_saveToGallery.setOnClickListener {
            val builder = AlertDialog.Builder(this@ImageActivity)
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
    }

    override fun showImageSavedToast() {
        Toast.makeText(this, "ImageActivity saved to gallery !", Toast.LENGTH_LONG).show()
    }

    override fun showErrorToast() {
        Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show()
    }

    override fun displayImage(bitmap: Bitmap) {
        this.iv_saveToGallery.setImageBitmap(bitmap)
    }

    override fun getContext(): Any {
        return applicationContext
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