package com.example.themoviedb.base

abstract class BaseRepository : BaseContract.BaseRepository {
    val remoteDataSource = RemoteDataSource.Instance
    val localDataSource = LocalDataSource.Instance
}