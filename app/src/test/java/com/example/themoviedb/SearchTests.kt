package com.example.themoviedb

import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class SearchTests {
    private lateinit var presenter: MainPresenter
    private lateinit var view: Contract.MainView
    private lateinit var model: Contract.MainModel

    @Before
    fun setup() {
        view = mock()
        model = mock()
        presenter = MainPresenter(view, model)
    }

    @Test
    fun search_with_non_empty_string_callsSetSearchFlag() {
        presenter.searchOnClicked("jason")
        verify(view).setSearchFlag(any())
    }

    @Test
    fun search_with_empty_string_never_callsSetSearchFlag() {
        presenter.searchOnClicked("")
        verify(view, never()).setSearchFlag(any())
    }
}