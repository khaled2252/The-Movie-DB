package com.example.themoviedb.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class BaseView<Presenter : BasePresenter<*, *>> : AppCompatActivity(),
    BaseContract.BaseIView {
    abstract override val presenter: Presenter
    abstract override fun onViewReady(savedInstanceState: Bundle?)

    @LayoutRes
    abstract override fun getLayoutResourceId(): Int

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        onViewReady(savedInstanceState)
        presenter.viewOnCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroy()
    }


}
