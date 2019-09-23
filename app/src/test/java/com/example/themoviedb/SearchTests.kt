package com.example.themoviedb

import android.content.Context
import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class SearchTests {
    private lateinit var presenter: MainPresenter
    private lateinit var view: Contract.MainView
    private lateinit var model: Contract.MainModel

    @Mock
    private lateinit var mockApplicationContext: Context

    @Before
    fun setup() {
        view = mock()
        model = mock()
        presenter = MainPresenter(view, model)
        MockitoAnnotations.initMocks(this) //To init context
    }

    @Test
    fun search_non_empty_string_callsFetchJson() {
        val currentPage = 1
        val query = "jason"

        presenter.searchOnClicked(query)
        `when`(mockApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn("")

        verify(model).fetchJson(
            eq(currentPage),
            eq(query),
            any()
        )
    }

    @Test
    fun search_empty_string_never_callsFetchJson() {
        val query = ""

        presenter.searchOnClicked(query)
        `when`(mockApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn("")
        verify(model, never()).fetchJson(
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyString(),
            any()
        )
    }
}