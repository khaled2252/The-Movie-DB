package com.example.themoviedb.utils.models

import com.google.gson.annotations.SerializedName

data class PopularPeopleResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: ArrayList<Person>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)