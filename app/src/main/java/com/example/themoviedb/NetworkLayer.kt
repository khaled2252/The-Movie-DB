package com.example.themoviedb

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable

interface RetrofitService {
    @GET("/3/person/popular?")
    fun getPopularPeople(@Query("api_key") apiKey: String, @Query("page") page: String): Call<PopularPeopleResponse>

    @GET("/3/person/popular?")
    fun getPopularPeopleSearh(@Query("api_key") apiKey: String, @Query("page") page: String, @Query("query")searchedWord: String?): Call<PopularPeopleResponse>

    @GET("/3/person/{profile_id}/images?")
    fun getPopularPersonProfiles(@Path("profile_id")profileId : String,@Query("api_key") apiKey: String): Call<PersonProfilesResponse>

    companion object {
        const val BASE_URL = "https://api.themoviedb.org"
        const val API_KEY = "3e68c56cf7097768305e38273efd342c"
        const val PROFILE_IMAGE = "https://image.tmdb.org/t/p/w300/"
    }
}
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
data class Person(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("gender")
    val gender: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("known_for")
    val knownFor: List<KnownFor>,
    @SerializedName("known_for_department")
    val knownForDepartment: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("profile_path")
    val profilePath: String
)

data class KnownFor(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
): Serializable

data class PersonProfilesResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("profiles")
    val profiles: ArrayList<Profile>
)

data class Profile(
    @SerializedName("aspect_ratio")
    val aspectRatio: Double,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("iso_639_1")
    val iso6391: Any,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("width")
    val width: Int
)
