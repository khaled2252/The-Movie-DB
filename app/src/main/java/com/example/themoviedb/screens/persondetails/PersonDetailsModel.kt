package com.example.themoviedb.screens.persondetails

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.themoviedb.network.PersonProfilesResponse
import com.example.themoviedb.network.Profile
import com.example.themoviedb.network.RetrofitClient
import com.example.themoviedb.network.RetrofitClient.Companion.API_KEY
import com.example.themoviedb.network.RetrofitClient.Companion.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PersonDetailsModel :
    Contract.PersonDetailsModel {
    override fun fetchJson(profileId: String, resultList: (ArrayList<Profile>?) -> Unit) {
        val apiService = RetrofitClient.getApiService(BASE_URL)

        val call: Call<PersonProfilesResponse> =
            apiService.getPopularPersonProfiles(profileId, API_KEY)

        call.enqueue(object : Callback<PersonProfilesResponse> {
            override fun onFailure(call: Call<PersonProfilesResponse>, t: Throwable) {
                Log.e("Response", "Failed to get response")
            }

            override fun onResponse(
                call: Call<PersonProfilesResponse>,
                response: Response<PersonProfilesResponse>
            ) {
                Log.i("Response", response.toString())
                resultList(response.body()?.profiles)
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

