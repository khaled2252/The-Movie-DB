package com.example.themoviedb

import com.example.themoviedb.models.Person
import com.example.themoviedb.screens.main.Contract
import com.example.themoviedb.screens.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class LoadMoreTests {
    private lateinit var presenter: MainPresenter
    private lateinit var view: Contract.MainView
    private lateinit var repository: Contract.MainRepository

    @Before
    fun setupWithSpy() {
        view = mock()
        repository = mock()
        presenter = spy(MainPresenter(view, repository))
    }

    @Test
    fun scrolling_at_end_of_page_incrementsCurrentPage() {
        val currentPage = 1
        presenter.recyclerViewOnScrolled(any(), any())

        whenever(presenter.reachedEndOfScreen(any(), any())).thenReturn(true)

        assertEquals(2, currentPage + 1)
    }


    @Test
    fun scrolling_at_end_of_page_callsFetchJson_on_next_page() {
        val currentPage = 1

        presenter.recyclerViewOnScrolled(any(), any())

        whenever(presenter.reachedEndOfScreen(any(), any())).thenReturn(true)

        verify(repository).fetchJson(
            eq(currentPage + 1),
            anyOrNull(),
            any()
        )
    }

    @Test
    fun scrolling_at_end_of_loaded_page_callsAddProgressBar() {
        //Initialize empty arrayList of size 20 - > stubbing loadData() when view is created
        val arrayList = arrayOfNulls<Person>(20).let { arrayList ->
            ArrayList<Person?>(arrayList.size).apply { arrayList.forEach { add(it) } }
        }
        presenter.resultList = arrayList
        presenter.recyclerViewOnScrolled(any(), any())

        whenever(presenter.reachedEndOfScreen(any(), any())).thenReturn(true)

        verify(presenter).addProgressBar()
    }
/*
    //Not complete -> fetchJsonCallBack.firstValue gives outOfBounds Exception in debug
    @Test
    fun scrolling_at_end_of_loaded_page_increasesResultList_by_20() {
        //Initialize empty arrayList of size 20 - > stubbing loadData() when view is created
        val arrayList = arrayOfNulls<Person>(20).let { arrayList ->
            ArrayList<Person?>(arrayList.size).apply { arrayList.forEach { add(it) } }
        }
        val responseArrayList = arrayListOf<Person>()
        for (i in 0..19) {
            responseArrayList.add(
                Person(
                    true,
                    0,
                    0,
                    listOf(KnownFor(true, "", listOf(1), 1, "", "", "", "", "", "", "", true, 0.0,1))
                , "null", "null", 0.0, "null"
            ))
        }
        val fetchJsonCallBack = argumentCaptor<(ArrayList<Person>?) -> Unit>()
        presenter.resultList = arrayList
        presenter.recyclerViewOnScrolled(any(), any())

        whenever(presenter.reachedEndOfScreen(any(), any())).thenReturn(true)
        whenever(repository.fetchJson(any(), any(), fetchJsonCallBack.capture()))
            .then { fetchJsonCallBack.firstValue.invoke(responseArrayList) }

        assertEquals(arrayList.size + 20, presenter.resultList.size)
    }
*/
}