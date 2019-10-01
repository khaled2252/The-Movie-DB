package com.example.themoviedb.screens.persondetails

import android.util.Log
import com.example.themoviedb.base.BaseRepository
import com.example.themoviedb.models.PersonProfilesResponse
import com.example.themoviedb.models.Profile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonDetailsRepository :
    BaseRepository(), Contract.PersonDetailsRepository {
    override fun getProfile(profileId: String, resultList: (ArrayList<Profile>?) -> Unit) {
        val apiService = remoteDataSource.api
        val call: Call<PersonProfilesResponse> =
            apiService.getPopularPersonProfiles(profileId)

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
        localDataSource.saveImageToStorage(arr)
    }
}

