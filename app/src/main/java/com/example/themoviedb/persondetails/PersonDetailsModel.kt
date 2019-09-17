package com.example.themoviedb.persondetails

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.themoviedb.PersonProfilesResponse
import com.example.themoviedb.Profile
import com.example.themoviedb.RetrofitService
import com.example.themoviedb.RetrofitService.Companion.API_KEY
import com.example.themoviedb.RetrofitService.Companion.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PersonDetailsModel : Contract.PersonDetailsModel {
    override fun fetchJson(profileId : String, resultList: (ArrayList<Profile>?)->Unit){
        val retrofit= Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RetrofitService::class.java)

        val call : Call<PersonProfilesResponse> =
            service.getPopularPersonProfiles(profileId,API_KEY)

        call.enqueue(object : Callback<PersonProfilesResponse> {
            override fun onFailure(call: Call<PersonProfilesResponse>, t: Throwable) {
                Log.e("Response","Failed to get response")
            }

            override fun onResponse(
                call: Call<PersonProfilesResponse>,
                response: Response<PersonProfilesResponse>
            ) {
                Log.i("Response",response.toString())
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

