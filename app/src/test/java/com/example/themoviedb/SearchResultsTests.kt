package com.example.themoviedb

import android.content.Context
import com.example.themoviedb.network.Person
import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.`when`


class SearchResultsTests {
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
        val fetchJsonCallBack = argumentCaptor<(ArrayList<Person>?) -> Unit>()

        presenter.searchOnClicked(query)
        `when`(mockApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn("")

        verify(model).fetchJson(
            eq(currentPage),
            eq(query),
            any()//fetchJsonCallBack.capture()
        )

        //TODO Handle callback
        //Now Callback is captured we can verify what happens after its invoked
        //fetchJsonCallBack.firstValue.invoke()
        //verify()
    }

    @Test
    fun search_empty_string_never_callsFetchJson() {
        val query = ""
        val fetchJsonCallBack = argumentCaptor<(ArrayList<Person>?) -> Unit>()

        presenter.searchOnClicked(query)
        `when`(mockApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn("")
        verify(model, never()).fetchJson(
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyString(),
            any()//fetchJsonCallBack.capture()
        )
    }
}