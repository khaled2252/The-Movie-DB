package com.example.themoviedb.persondetails

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.themoviedb.KnownFor
import com.example.themoviedb.Profile
import com.example.themoviedb.R
import com.example.themoviedb.RetrofitService
import com.example.themoviedb.image.ImageActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_person_details.*
import java.lang.Exception


class PersonDetailsView : AppCompatActivity(), Contract.PersonDetailsView {
    private lateinit var presenter: PersonDetailsPresenter
    private lateinit var profileId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)

        presenter = PersonDetailsPresenter(this, PersonDetailsModel())

       val mRecyclerView = this.rv_pictures
        mRecyclerView.apply {
            layoutManager =
                GridLayoutManager(this@PersonDetailsView, 3)
            adapter = PopularPersonAdapter(presenter.resultList)
            setItemViewCacheSize(50)

            //To remove spaces in grid view
            mRecyclerView.addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    val spaceInPixels = 10
                    override fun getItemOffsets(
                        outRect: Rect, view: View,
                        parent: RecyclerView, state: RecyclerView.State
                    ) {
                        outRect.left = spaceInPixels
                        outRect.right = spaceInPixels
                        outRect.bottom = spaceInPixels

                        if (parent.getChildLayoutPosition(view) == 0) {
                            outRect.top = spaceInPixels
                        } else {
                            outRect.top = 0
                        }
                    }
                }
            )
        }

        presenter.viewOnCreated()

    }

    override fun setUiFromIntent() {
        profileId = intent.getStringExtra("profile_id")
        val personName = intent.getStringExtra("person_name")
        val popularity = intent.getStringExtra("popularity")
        val knownFor = intent.getSerializableExtra("known_for") as ArrayList<KnownFor>?
        val knownForDepartment = intent.getStringExtra("known_for_department")

        val photoPath = openFileInput("profile_picture")
        val bitmap = BitmapFactory.decodeStream(photoPath)

        iv_profileImage.setImageBitmap(bitmap)
        tv_name.text = personName

        val knownForArrayList: ArrayList<String> = ArrayList()
        if (knownFor != null) {
            for (i in 0 until knownFor.size)
                knownForArrayList.add(knownFor[i].originalTitle)
        } else knownForArrayList.add("No movies found")

        tv_knownFor.text =
            StringBuilder("$personName is known for $knownForDepartment in $knownForArrayList with popularity score of $popularity")
    }

    override fun getProfileId(): String {
        return profileId
    }

    override fun navigateToImageActivity() {
        val intent = Intent(applicationContext, ImageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    override fun notifyItemRangeInsertedFromRecyclerView(start: Int, itemCount: Int) {
        rv_pictures.adapter?.notifyItemRangeInserted(start, itemCount)
    }

    override fun notifyItemRemovedFromRecyclerView(index: Int) {
        rv_pictures.adapter?.notifyItemRemoved(index)
    }

    override fun notifyItemRangeChangedInRecyclerView(itemCount: Int) {
        rv_pictures.adapter?.notifyItemRangeChanged(
            rv_pictures.adapter!!.itemCount,
            itemCount
        )
    }

    inner class PopularPersonAdapter(private val list: List<Profile?>) :
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
                    (holder.itemView.findViewById<ImageView>(R.id.iv_image).drawable as BitmapDrawable).bitmap
                presenter.itemViewOnClick(arrayOf(holder.itemView.context, bitmap))

            }
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            private val mImageView: ImageView = mView.findViewById(R.id.iv_image)
            private val mProgressBar: ProgressBar = mView.findViewById(R.id.progressBar)
            fun bind(personImages: Profile) {
                Picasso.get()
                    .load(RetrofitService.PROFILE_IMAGE +personImages.filePath)
                    .into(mImageView,object : Callback {
                        override fun onSuccess() {
                            mProgressBar.visibility=View.GONE
                        }

                        override fun onError(e: Exception?) {
                            mImageView.setImageBitmap(
                                BitmapFactory.decodeResource(applicationContext.resources,
                                    R.drawable.no_image))
                        }
                    })
            }
        }
    }
}




