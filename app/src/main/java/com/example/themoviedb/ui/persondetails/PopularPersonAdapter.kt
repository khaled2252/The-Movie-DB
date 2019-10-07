package com.example.themoviedb.ui.persondetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.R
import com.example.themoviedb.utils.TMDBApplication
import com.example.themoviedb.utils.models.Profile
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class PopularPersonAdapter(
    private val presenter: PersonDetailsPresenter,
    private val list: List<Profile?>

) :
    RecyclerView.Adapter<PopularPersonAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personImages: Profile? = list[position]
        holder.bind(personImages!!)
        holder.itemView.setOnClickListener {
            val bitmap =
                (holder.itemView.findViewById<ImageView>(R.id.iv_image).drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) { //To avoid clicking while bitmap is not loaded yet
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val imageByteArray = stream.toByteArray()
                presenter.itemViewOnClick(imageByteArray)
            }

        }
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val mImageView: ImageView = mView.findViewById(R.id.iv_image)
        private val mProgressBar: ProgressBar = mView.findViewById(R.id.progressBar)
        fun bind(personImages: Profile) {
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w300/" + personImages.filePath)
                .into(mImageView, object : Callback {
                    override fun onSuccess() {
                        mProgressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        mImageView.setImageBitmap(
                            BitmapFactory.decodeResource(
                                TMDBApplication.instance.resources,
                                R.drawable.no_image
                            )
                        )
                    }
                })
        }
    }
}