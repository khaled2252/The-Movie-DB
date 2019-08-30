package com.example.themoviedb

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.themoviedb.pojos.Person

class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
    private var mNameView: TextView? = null
    private var mKnownForDepartementView: TextView? = null

    init {
        mNameView = itemView.findViewById(R.id.tv_name)
        mKnownForDepartementView= itemView.findViewById(R.id.tv_known_for_department)
    }

    fun bind(person : Person) {
        mNameView?.text = person.name
        mKnownForDepartementView?.text = person.known_for_department
    }

}