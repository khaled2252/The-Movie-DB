package com.example.httpurlconnection

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.httpurlconnection.Pojos.Result

class PopularPeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.row_layout, parent, false)) {
    private var mNameView: TextView? = null
    private var mKnownForDepartementView: TextView? = null

    init {
        mNameView = itemView.findViewById(R.id.tv_name)
        mKnownForDepartementView= itemView.findViewById(R.id.tv_known_for_department)
    }

    fun bind(result : Result) {
        mNameView?.text = result.name
        mKnownForDepartementView?.text = result.known_for_department
    }

}