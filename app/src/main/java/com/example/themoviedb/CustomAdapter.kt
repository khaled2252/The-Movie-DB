package com.example.themoviedb

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.themoviedb.pojos.Person

class CustomAdapter(private val list: List<Person>): RecyclerView.Adapter<PopularPeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularPeopleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PopularPeopleViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: PopularPeopleViewHolder, position: Int) {
        val person :Person = list[position]
        holder.bind(person)
    }
    override fun getItemCount(): Int = list.size

}