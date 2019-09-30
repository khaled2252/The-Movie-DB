package com.example.themoviedb.base

abstract class BaseRepository : BaseContract.BaseIRepository {
    val remoteDataSource = RemoteDataSource.Instance
    val localDataSource = LocalDataSource.Instance
}