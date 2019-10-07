package com.example.themoviedb.dagger

import com.example.themoviedb.ui.main.MainPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideBasePresenter(): BaseP = MainPresenter()
}