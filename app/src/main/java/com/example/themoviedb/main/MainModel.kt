package com.example.themoviedb.main

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable


class MainModel : Contract.MainModel {
    private lateinit var picasso : Picasso
    private lateinit var retrofit : Retrofit
    override fun fetchImage(path : String,imageFetched : com.squareup.picasso.Callback){
        picasso = Picasso.get()
        picasso.load(PROFILE_IMAGE+path).fetch(imageFetched)
    }
    override fun fetchData(currentPage: Int, searchedWord: String?, resultList: (ArrayList<Person>?)->Unit){
        retrofit=Retrofit.Builder()
            .baseUrl(POPULAR_PEOPLE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetrofitService::class.java)

        val call : Call<PopularPeopleResponse> = if (searchedWord == null) {
            service.getPopularPeople(API_KEY,currentPage.toString())
        } else
            service.getPopularPeopleSearh(API_KEY,currentPage.toString(),searchedWord)

        call.enqueue(object : Callback<PopularPeopleResponse> {
            override fun onFailure(call: Call<PopularPeopleResponse>, t: Throwable) {
                Log.e("Response","Failed to get response")
            }

            override fun onResponse(
                call: Call<PopularPeopleResponse>,
                response: Response<PopularPeopleResponse>
            ) {
                Log.i("Response",response.toString())
                resultList(response.body()?.results)
            }
        })
    }

    override fun saveImage(arr: Array<Any>) {
        lateinit var fos: FileOutputStream
        val context: Context = arr[0] as Context
        val b: Bitmap = arr[1] as Bitmap

        try {
            fos = context.openFileOutput("profile_picture", Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos.close()
        }
    }

    companion object {
        const val POPULAR_PEOPLE = "https://api.themoviedb.org/"
        const val API_KEY = "3e68c56cf7097768305e38273efd342c"
        const val PROFILE_IMAGE = "https://image.tmdb.org/t/p/w300/"
    }
}

interface RetrofitService {
    @GET("3/person/popular?")
    fun getPopularPeople(@Query("api_key") apiKey: String, @Query("page") page: String): Call<PopularPeopleResponse>

    @GET("3/person/popular?")
    fun getPopularPeopleSearh(@Query("api_key") apiKey: String, @Query("page") page: String,@Query("query")searchedWord: String?): Call<PopularPeopleResponse>

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
):Serializable


