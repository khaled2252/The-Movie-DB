package com.example.themoviedb.dagger

import com.example.themoviedb.base.BaseActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, PresenterModule::class])
interface AppComponent{
    fun inject(target: BaseActivity<*>)
}