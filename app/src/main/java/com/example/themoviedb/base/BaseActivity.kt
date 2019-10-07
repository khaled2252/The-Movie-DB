package com.example.themoviedb.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.themoviedb.utils.TMDBApplication
import javax.inject.Inject

abstract class BaseActivity<Presenter : BasePresenter<*, *>> : AppCompatActivity(),
    BaseContract.BaseView {
    @Inject
    override lateinit var presenter : Presenter

    abstract override fun onViewReady(savedInstanceState: Bundle?)

    @LayoutRes
    abstract override fun getLayoutResourceId(): Int

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        (application as TMDBApplication).appComponent.inject(this)

        onViewReady(savedInstanceState)
        presenter.viewOnCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroy()
    }
}