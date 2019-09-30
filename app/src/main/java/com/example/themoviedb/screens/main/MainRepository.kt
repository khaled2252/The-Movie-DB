package com.example.themoviedb.screens.main

import android.util.Log
import com.example.themoviedb.base.BaseRepository
import com.example.themoviedb.models.Person
import com.example.themoviedb.models.PopularPeopleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainRepository : BaseRepository(),
     Contract.MainRepository {

    override fun fetchJson(
        currentPage: Int,
        searchedWord: String?,
        resultList: (ArrayList<Person>?) -> Unit
    ) {
        val apiService = remoteDataSource.api
        val call: Call<PopularPeopleResponse> = if (searchedWord == null) {
            apiService.getPopularPeople(currentPage.toString())
        } else
            apiService.getPopularPeopleSearch(currentPage.toString(), searchedWord)

        call.enqueue(object : Callback<PopularPeopleResponse> {
            override fun onFailure(call: Call<PopularPeopleResponse>, t: Throwable) {
                Log.e("Response", "Failed to get response")
            }

            override fun onResponse(
                call: Call<PopularPeopleResponse>,
                response: Response<PopularPeopleResponse>
            ) {
                Log.i("Response", response.toString())
                resultList(response.body()?.results)
            }
        })
    }

    override fun saveImage(arr: Array<Any>) {
        localDataSource.saveImageToStorage(arr)
    }
}




