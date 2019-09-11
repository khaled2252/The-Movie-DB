package com.example.themoviedb.persondetails

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.themoviedb.R
import kotlinx.android.synthetic.main.activity_person_details.*


class PersonDetailsActivity : AppCompatActivity() {

    private lateinit var personDetailsController: PersonDetailsController
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_details)

        personDetailsController = PersonDetailsController(this)

        personDetailsController.setUiFromIntent()

        mRecyclerView = this.rv_pictures

        mRecyclerView.apply {
            layoutManager = GridLayoutManager(this@PersonDetailsActivity, 3)
            this.setItemViewCacheSize(50)
            personDetailsController.setRecyclerViewAdapter()

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

        //Load images to be put in grid view
        requestPersonImages()
    }

    fun notifyDataSetChangedInRecyclerView(){
        this.rv_pictures.adapter?.notifyDataSetChanged()
    }
    private fun requestPersonImages() {
        personDetailsController.loadData {
            if (it) notifyDataSetChangedInRecyclerView()
        }
    }
}


