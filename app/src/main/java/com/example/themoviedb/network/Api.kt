package com.example.themoviedb.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    companion object Factory {
        fun create(): Api {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(Api::class.java)
        }

        private const val BASE_URL = "https://api.themoviedb.org"
        const val API_KEY = "3e68c56cf7097768305e38273efd342c"
    }

}