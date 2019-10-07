package com.example.themoviedb.base

import com.example.themoviedb.utils.TMDBApplication
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

abstract class BasePresenter<View : BaseContract.BaseView, Repository : BaseContract.BaseRepository>
    : BaseContract.BasePresenter {
    @Inject
    override lateinit var view: View
    @Inject
    override
    lateinit var repository: Repository

    private val compositeDisposable = CompositeDisposable()

    init {
        (application as TMDBApplication).appComponent.inject(this)

    }
    abstract override fun viewOnCreated()

    override fun onViewDestroy() {
        //view = null
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