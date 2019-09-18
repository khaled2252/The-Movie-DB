package com.example.themoviedb.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/3/person/popular?")
    fun getPopularPeople(@Query("api_key") apiKey: String, @Query("page") page: String): Call<PopularPeopleResponse>

    @GET("/3/search/person?")
    fun getPopularPeopleSearch(
        @Query("api_key") apiKey: String, @Query("page") page: String, @Query(
            "query"
        ) searchedWord: String?
    ): Call<PopularPeopleResponse>

    @GET("/3/person/{profile_id}/images?")
    fun getPopularPersonProfiles(@Path("profile_id") profileId: String, @Query("api_key") apiKey: String): Call<PersonProfilesResponse>

}