package com.example.themoviedb.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query


class MainModel : Contract.MainModel {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(RetrofitService::class.java)

    override fun enqueueCall(currentPage: Int,searchedWord: String?,resultList: (ArrayList<Person>?)->Unit){
        var call : Call<PopularPeopleResponse> = if (searchedWord == null) {
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
    override fun fetchJson(
        currentPage: Int,
        searchedWord: String?,
        fetchedData: (String?) -> Unit
    ) {
        val fetchJsonObject = FetchJson(object : FetchDataCallBack {
            override fun onFetched(fetchedData: String?) {
                fetchedData(fetchedData)
            }
        })
        if (searchedWord == null)
            fetchJsonObject.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                currentPage.toString()
            )
        else
            fetchJsonObject.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                currentPage.toString(),
                searchedWord
            )
    }

    override fun fetchImage(path: String, fetchedImage: (Any?) -> Unit) {
        FetchImage(object : FetchImageCallBack {
            override fun onFetched(fetchedImage: Any?) {
                fetchedImage(fetchedImage)
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path)
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

    class FetchJson(fetchDataCallBack: FetchDataCallBack) :
        AsyncTask<String, String, String?>() { //takes reference from callback interface

        private var delegate = fetchDataCallBack //Assigning callback interface through constructor
        private var body: StringBuffer = StringBuffer()
        private lateinit var url: String

        override fun doInBackground(vararg params: String): String? {
            try {
                //params[0] is page number , params[1] is search queryz
                url = if (params.size == 1)
                    Companion.POPULAR_PEOPLE + Companion.API_KEY + Companion.PAGE_ATTRIBUTE + params[0]
                else
                    Companion.SEARCH_PERSON_NAME + params[1] + Companion.PAGE_ATTRIBUTE + params[0]

                val urlConnection = URL(url).openConnection() as HttpURLConnection

                try {
                    val input = BufferedReader(
                        InputStreamReader(urlConnection.inputStream)
                    )

                    var inputLine: String?
                    do {
                        inputLine = input.readLine()
                        body.append(inputLine)
                    } while (inputLine != null)
                    input.close()
                } finally {
                    urlConnection.disconnect()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return body.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            delegate.onFetched(result)
        }
    }

    class FetchImage(FetchImageCallBack: FetchImageCallBack) : AsyncTask<String, Void, Bitmap?>() {
        private var delegate = FetchImageCallBack
        override fun doInBackground(vararg params: String): Bitmap? {
            var bmp: Bitmap? = null
            try {
                val input = URL(Companion.PROFILE_IMAGE_PATH + params[0]).openStream()
                bmp = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bmp
        }

        override fun onPostExecute(result: Bitmap?) {
            delegate.onFetched(result)
        }
    }

    companion object {
        const val API_KEY = "3e68c56cf7097768305e38273efd342c"
        const val PAGE_ATTRIBUTE = "&page="
        const val POPULAR_PEOPLE = "https://api.themoviedb.org/3/person/popular?"
        const val SEARCH_PERSON_NAME =
            "https://api.themoviedb.org/3/search/person?api_key=e6f20f39139b1f5a2be132cbaaa9ce43&query="
        const val PROFILE_IMAGE_PATH = "https://image.tmdb.org/t/p/w300/"
    }
}

interface FetchImageCallBack {
    fun onFetched(fetchedImage: Any?)//Callback method which is called at the object which implements the interface
}

interface FetchDataCallBack {
    fun onFetched(fetchedData: String?)
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


