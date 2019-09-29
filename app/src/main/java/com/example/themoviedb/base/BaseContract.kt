package com.example.mvp.base

import android.os.Bundle
import androidx.annotation.LayoutRes

interface BaseContract {
    interface BaseIView {

        val presenter: BaseIPresenter

        fun showLoading()
        fun hideLoading()
        fun onViewReady(savedInstanceState: Bundle?)

        @LayoutRes
        fun getLayoutResourceId(): Int
    }

    interface BaseIPresenter {
        fun onViewReady()
    }

    interface BaseIRepository {

    }
}
