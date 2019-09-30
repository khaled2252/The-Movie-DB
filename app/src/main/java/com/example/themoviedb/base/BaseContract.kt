package com.example.themoviedb.base

import android.os.Bundle
import androidx.annotation.LayoutRes

interface BaseContract {
    interface BaseIView {

        val presenter: BaseIPresenter

        fun onViewReady(savedInstanceState: Bundle?)

        @LayoutRes
        fun getLayoutResourceId(): Int
    }

    interface BaseIPresenter {
        fun viewOnCreated()
        fun onViewDestroy()
    }

    interface BaseIRepository {

    }
}
