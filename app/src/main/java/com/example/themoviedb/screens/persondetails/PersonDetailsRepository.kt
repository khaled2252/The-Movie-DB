package com.example.themoviedb.screens.persondetails

import android.graphics.Bitmap
import com.example.themoviedb.base.BaseRepository
import com.example.themoviedb.utils.models.PersonProfilesResponse
import io.reactivex.Single

class PersonDetailsRepository :
    BaseRepository(), Contract.PersonDetailsRepository {
    override fun getProfile(profileId: String): Single<PersonProfilesResponse> {
        val apiService = remoteDataSource.api
        return apiService.getPopularPersonProfiles(profileId)
    }

    override fun saveImage(image: Bitmap) {
        localDataSource.saveImageToStorage(image)
    }
}