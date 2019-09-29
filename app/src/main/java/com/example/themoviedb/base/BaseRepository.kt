package com.example.mvp.base

abstract class BaseRepository : BaseContract.BaseIRepository {
    val remoteDataSource = RemoteDataSource.Instance
    val localDataSource = LocalDataSource.Instance
}