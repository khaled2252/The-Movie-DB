package com.example.httpurlconnection

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.httpurlconnection.Pojos.Result

class CustomAdapter(private val list: List<Result>): RecyclerView.Adapter<PopularPeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularPeopleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PopularPeopleViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: PopularPeopleViewHolder, position: Int) {
        val result :Result = list[position]
        holder.bind(result)
    }
    override fun getItemCount(): Int = list.size

}