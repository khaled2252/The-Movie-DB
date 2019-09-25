package com.example.themoviedb

import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class PullToRefreshTests {
    private lateinit var presenter: MainPresenter
    private lateinit var view: Contract.MainView
    private lateinit var model: Contract.MainModel

    @Before
    fun setup() {
        view = mock()
        model = mock()
        presenter = spy(MainPresenter(view, model)) //spy to verify methods on a non mocked object (sut)
    }

    @Test
    fun pull_to_refresh_clearsList() {
        presenter.layoutOnRefreshed("")
        assertEquals(0, presenter.resultList.size)
    }

    @Test
    fun pull_to_refresh_resetsPageCount() {
        presenter.layoutOnRefreshed("")
        assertEquals(1, presenter.currentPage)
    }

    @Test
    fun pull_to_refresh_on_default_data_callsFetchJson_with_empty_query() {
        val query = ""
        presenter.layoutOnRefreshed(query)
        verify(model).fetchJson(
            any(),
            eq(null),
            any()
        )
    }

    @Test
    fun pull_to_refresh_on_search_data_callsFetchJson_with_nonempty_query() {
        val query = "jason"
        presenter.layoutOnRefreshed(query)
        verify(model).fetchJson(
            any(),
            eq(query),
            any()
        )
    }
}
