package com.example.themoviedb.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviedb.R
import com.example.themoviedb.utils.ApplicationSingleton
import com.example.themoviedb.utils.models.Person
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class PopularPeopleAdapter(
    private val presenter: MainPresenter, private var list: ArrayList<Person?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewItem = 1
    private val viewProgress = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        viewHolder = if (viewType == viewItem) {
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_layout, parent, false
            )
            PopularPeopleViewHolder(inflater, parent)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.progress_bar, parent, false
            )
            ProgressViewHolder(view)
        }
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] != null) viewItem else viewProgress
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PopularPeopleViewHolder) {
            val person: Person? = list[position]
            holder.bind(person!!)
            holder.itemView.setOnClickListener {
                val bitmap =
                    (holder.itemView.findViewById<ImageView>(R.id.iv_profile).drawable as? BitmapDrawable)?.bitmap
                if (bitmap != null) { //To avoid clicking while bitmap is not loaded yet
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val imageByteArray = stream.toByteArray()
                    presenter.itemViewOnClick(imageByteArray, person)
                }
            }
        } else {
            (holder as ProgressViewHolder).progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int = list.size

    inner class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
        private var mNameView: TextView? = null
        private var mKnownForDepartmentView: TextView? = null
        private var mProfilePicture: ImageView? = null
        private var mProgressBar: ProgressBar

        init {
            mNameView = itemView.findViewById(R.id.tv_name)
            mKnownForDepartmentView = itemView.findViewById(R.id.tv_known_for_department)
            mProfilePicture = itemView.findViewById(R.id.iv_profile)
            mProgressBar = itemView.findViewById(R.id.progressBar)
        }

        fun bind(person: Person) {
            mNameView?.text = person.name
            mKnownForDepartmentView?.text = person.knownForDepartment
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w300/" + person.profilePath)
                .into(mProfilePicture, object : Callback {
                    override fun onSuccess() {
                        mProgressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        mProfilePicture?.setImageBitmap(
                            BitmapFactory.decodeResource(
                                ApplicationSingleton.instance.resources,
                                R.drawable.no_image
                            )
                        )
                    }
                })
        }
    }

    inner class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var progressBar: ProgressBar = v.findViewById(R.id.pb)
    }
}