package com.example.themoviedb.models

import com.google.gson.annotations.SerializedName

data class PersonProfilesResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("profiles")
    val profiles: ArrayList<Profile>
)