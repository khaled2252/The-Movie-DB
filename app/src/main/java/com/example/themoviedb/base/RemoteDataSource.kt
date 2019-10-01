package com.example.themoviedb.base

import com.example.themoviedb.BuildConfig
import com.example.themoviedb.models.PersonProfilesResponse
import com.example.themoviedb.models.PopularPeopleResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class RemoteDataSource {
    companion object {
        val Instance = RemoteDataSource()
    }

    val api = Api.create()

    interface Api {

        @GET("person/popular?")
        fun getPopularPeople(@Query("page") page: String): Single<PopularPeopleResponse>

        @GET("search/person?")
        fun getPopularPeopleSearch(
            @Query("page") page: String, @Query(
                "query"
            ) searchedWord: String?
        ): Single<PopularPeopleResponse>

        @GET("person/{profile_id}/images?")
        fun getPopularPersonProfiles(@Path("profile_id") profileId: String): Single<PersonProfilesResponse>

        companion object Factory {
            private val loggingInterceptor: HttpLoggingInterceptor
                get() {
                    val log = HttpLoggingInterceptor()
                    log.level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
                    return log
                }

            private val okHttpClient =
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()

                        val url = request.url

                        val newUrl = url.newBuilder()
                            .addQueryParameter(PARAM_API_KEY, API_KEY).build()

                        val builder = request.newBuilder()
                        builder.url(newUrl)

                        chain.proceed(builder.build())
                    }
                    .addInterceptor(loggingInterceptor)
                    .callTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build()

            fun create(): Api {

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()

                return retrofit.create(Api::class.java)
            }

            private const val BASE_URL = "https://api.themoviedb.org/3/"
            private const val API_KEY = "3e68c56cf7097768305e38273efd342c"
            private const val PARAM_API_KEY = "api_key"
        }

    }

}