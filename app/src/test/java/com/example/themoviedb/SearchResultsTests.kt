package com.example.themoviedb

import android.content.Context
import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class SearchResultsTests {
    @Mock private lateinit var mockApplicationContext: Context
    private lateinit var presenter: MainPresenter
    private lateinit var view:Contract.MainView
    private lateinit var model: Contract.MainModel

    @Before
    fun setup() {
        view = mock()
        model = mock()
        presenter = MainPresenter(view, model)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun search_non_empty_string_callsFetchJson() {
        val query ="jason"
        presenter.searchOnClicked(query)
        `when`(mockApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(any())
        verify(model).fetchJson(eq(1),eq(query), any())

    }
}