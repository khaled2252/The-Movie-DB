package com.example.themoviedb

import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString


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
    fun search_non_empty_string_callsFetchJson() {
        //given
        val currentPage = 1
        val query = "jason"

        //when
        presenter.searchOnClicked(query)

        //then
        verify(model).fetchJson(
            eq(currentPage),
            eq(query),
            any()
        )
    }

    @Test
    fun search_empty_string_never_callsFetchJson() {
        //given
        val query = ""

        //when
        presenter.searchOnClicked(query)

        //then
        verify(model, never()).fetchJson(
            anyInt(),
            anyString(),
            any()
        )
    }
}