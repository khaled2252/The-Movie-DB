package com.example.themoviedb.screens.main

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.themoviedb.network.Api
import com.example.themoviedb.network.Api.Factory.API_KEY
import com.example.themoviedb.network.Person
import com.example.themoviedb.network.PopularPeopleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


open class MainModel : Contract.MainModel {

    override fun fetchJson(
        currentPage: Int,
        searchedWord: String?,
        resultList: (ArrayList<Person>?) -> Unit
    ) {
        val apiService = Api.create()
        val call: Call<PopularPeopleResponse> = if (searchedWord == null) {
            apiService.getPopularPeople(API_KEY, currentPage.toString())
        } else
            apiService.getPopularPeopleSearch(API_KEY, currentPage.toString(), searchedWord)

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
}




