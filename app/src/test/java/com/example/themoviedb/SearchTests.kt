package com.example.themoviedb

import com.example.themoviedb.ui.main.Contract
import com.example.themoviedb.ui.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString


class SearchTests {
    private lateinit var presenter: MainPresenter
    private lateinit var view: Contract.MainView
    private lateinit var repository: Contract.MainRepository

    @Before
    fun setup() {
        view = mock()
        repository = mock()
        presenter = MainPresenter(view, repository)
    }

    @Test
    fun search_non_empty_string_callsFetchJson() {
        //given
        val currentPage = 1
        val query = "jason"

        //when
        presenter.searchOnClicked(query)

        //then
        verify(repository).getPopularPeople(
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
        verify(repository, never()).getPopularPeople(
            anyInt(),
            anyString(),
            any()
        )
    }
}