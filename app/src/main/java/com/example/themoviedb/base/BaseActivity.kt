package com.example.mvp.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity<Presenter : BasePresenter<*, *>> : AppCompatActivity(),
    BaseContract.BaseIView {
    abstract override val presenter: Presenter
    abstract override fun onViewReady(savedInstanceState: Bundle?)

    @LayoutRes
    abstract override fun getLayoutResourceId(): Int

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        //Implement some code

        onViewReady(savedInstanceState)
        presenter.onViewReady()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroy()
    }

    override fun showLoading() {
        TODO()
    }

    override fun hideLoading() {
        TODO()
    }

}
