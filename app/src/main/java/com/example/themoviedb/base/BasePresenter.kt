package com.example.themoviedb.base

abstract class BasePresenter<View : BaseContract.BaseIView, Repository : BaseContract.BaseIRepository>(
    var view: View?,
    val repository: Repository
) : BaseContract.BaseIPresenter {

    abstract override fun onViewReady()
    fun onViewDestroy() {
        view = null
    }
}