package com.example.httpurlconnection.Pojos

import java.io.Serializable

class ResponsePojo : Serializable {
    private val page: String? = null

    private val total_pages: String? = null

    private val results: Array<Result>? = null

    private val total_results: String? = null
    fun getPage(): String? {
        return page
    }

    fun getTotal_pages(): String? {
        return total_pages
    }

    fun getResults(): Array<Result>? {
        return this.results
    }

    fun getTotal_results(): String? {
        return total_results
    }
}