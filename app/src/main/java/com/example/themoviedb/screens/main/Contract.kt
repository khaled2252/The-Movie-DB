package com.example.themoviedb.screens.main

import com.example.themoviedb.network.Person

interface Contract {
    interface MainModel {
        fun fetchJson(
            currentPage: Int,
            searchedWord: String?,
            jsonFetched: (ArrayList<Person?>?) -> Unit
        )

        fun saveImage(arr: Array<Any>)
    }

}