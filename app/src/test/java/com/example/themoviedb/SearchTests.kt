package com.example.themoviedb

import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class SearchTests {
    @Test
    fun search_with_non_empty_string_callsSetSearchFlag() {
        val view= mock<Contract.MainView>()
        val model= mock<Contract.MainModel>()
        val presenter = MainPresenter(view, model)

        presenter.searchOnClicked("jason")
        verify(view).setSearchFlag(any())
    }

    @Test
    fun search_with_empty_string_callsSetSearchFlag() {
        val view= mock<Contract.MainView>()
        val model= mock<Contract.MainModel>()
        val presenter = MainPresenter(view, model)

        presenter.searchOnClicked("")
        verify(view, never()).setSearchFlag(any())
    }
}