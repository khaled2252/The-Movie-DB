package com.example.themoviedb.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private fun getRetrofitClient(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getApiService(baseUrl: String): Api {
            return getRetrofitClient(baseUrl).create(Api::class.java)

        }

        const val BASE_URL = "https://api.themoviedb.org"
        const val API_KEY = "3e68c56cf7097768305e38273efd342c"
    }
}

