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
import com.example.themoviedb.R
import com.example.themoviedb.image.ImageActivity
import com.example.themoviedb.main.KnownFor
import com.example.themoviedb.pojos.PersonImages
import kotlinx.android.synthetic.main.activity_person_details.*


class PersonDetailsView : AppCompatActivity(), Contract.PersonDetailsView {
    private lateinit var presenter: PersonDetailsPresenter
    private lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var profileId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)

        presenter = PersonDetailsPresenter(this, PersonDetailsModel())

        mRecyclerView = this.rv_pictures
        mRecyclerView.apply {
            layoutManager =
                androidx.recyclerview.widget.GridLayoutManager(this@PersonDetailsView, 3)
            adapter = PopularPersonAdapter(presenter.resultList)
            setItemViewCacheSize(50)

            //To remove spaces in grid view
            mRecyclerView.addItemDecoration(
                object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                    val spaceInPixels = 10
                    override fun getItemOffsets(
                        outRect: Rect, view: View,
                        parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State
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
        this.mRecyclerView.adapter?.notifyItemRangeInserted(start, itemCount)
    }

    override fun notifyItemRemovedFromRecyclerView(index: Int) {
        this.mRecyclerView.adapter?.notifyItemRemoved(index)
    }

    override fun notifyItemRangeChangedInRecyclerView(itemCount: Int) {
        this.mRecyclerView.adapter?.notifyItemRangeChanged(
            this.mRecyclerView.adapter!!.itemCount,
            itemCount
        )
    }

    inner class PopularPersonAdapter(private val list: List<PersonImages?>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<PopularPersonAdapter.ViewHolder>() {
        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val personImages: PersonImages? = list[position]
            holder.bind(personImages!!)
            holder.itemView.setOnClickListener {
                val bitmap =
                    (holder.itemView.findViewById<ImageView>(R.id.iv_image).drawable as BitmapDrawable).bitmap
                presenter.itemViewOnClick(arrayOf(holder.itemView.context, bitmap))

            }
        }

        inner class ViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
            private val mImageView: ImageView = mView.findViewById(R.id.iv_image)
            private val mProgressBar: ProgressBar = mView.findViewById(R.id.progressBar)
            fun bind(personImages: PersonImages) {
                presenter.loadImage(personImages.filePath) {
                    mProgressBar.visibility = View.GONE
                    if (it != null) {
                        mImageView.setImageBitmap(it as Bitmap)
                    } else
                        mImageView.setImageResource(R.drawable.no_image)
                }
            }
        }
    }
}




