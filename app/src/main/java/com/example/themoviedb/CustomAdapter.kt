package com.example.themoviedb

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.themoviedb.pojos.Person
import android.view.View
import android.widget.ProgressBar

class CustomAdapter(private val list: List<Person?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        val vh: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        if (viewType === VIEW_ITEM) {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.row_layout, parent, false
            )
            vh = PopularPeopleViewHolder(inflater,parent)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(
                R.layout.progress_bar, parent, false
            )

            vh = ProgressViewHolder(v)
        }
        return vh
    }
    override fun getItemViewType(position: Int): Int {
        return if (list[position] != null) VIEW_ITEM else VIEW_PROG
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is PopularPeopleViewHolder) {
        val person : Person? = list[position]
        holder.bind(person!!)
        } else {
            (holder as ProgressViewHolder).progressBar.isIndeterminate = true
        }
    }
    override fun getItemCount(): Int = list.size

}

class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var progressBar: ProgressBar

    init {
        progressBar = v.findViewById(R.id.pb)
    }
}