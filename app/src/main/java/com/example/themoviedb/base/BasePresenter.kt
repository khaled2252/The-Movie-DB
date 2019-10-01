package com.example.themoviedb.base

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

abstract class BasePresenter<View : BaseContract.BaseIView, Repository : BaseContract.BaseIRepository>(
    var view: View?,
    val repository: Repository
) : BaseContract.BaseIPresenter {
    private val compositeDisposable = CompositeDisposable()

    abstract override fun viewOnCreated()

    override fun onViewDestroy() {
        view = null
        compositeDisposable.clear()
    }

    fun <T> subscribe(
        observable: Single<T>,
        onSuccess: Consumer<T>,
        onError: Consumer<Throwable> = Consumer { }
    ) {
        compositeDisposable.add(
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError)
        )
    }

}