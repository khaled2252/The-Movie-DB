package com.example.httpurlconnection.Pojos

import java.io.Serializable

class Result : Serializable {
    private var overview: String? = null

    private var original_language: String? = null

    private var original_title: String? = null

    private var video: String? = null

    private var title: String? = null

    private var genre_ids: List<String>? = null

    private var poster_path: String? = null

    private var backdrop_path: String? = null

    private var release_date: String? = null

    private var popularity: String? = null

    private var vote_average: String? = null

    private var id: String? = null

    private var adult: String? = null

    private var vote_count: String? = null

    fun getOverview(): String? {
        return overview
    }


    fun getOriginal_language(): String? {
        return original_language
    }


    fun getOriginal_title(): String? {
        return original_title
    }


    fun getVideo(): String? {
        return video
    }


    fun getTitle(): String? {
        return title
    }


    fun getGenre_ids(): List<String>? {
        return genre_ids
    }


    fun getPoster_path(): String? {
        return poster_path
    }


    fun getBackdrop_path(): String? {
        return backdrop_path
    }


    fun getRelease_date(): String? {
        return release_date
    }


    fun getPopularity(): String? {
        return popularity
    }


    fun getVote_average(): String? {
        return vote_average
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getAdult(): String? {
        return adult
    }


    fun getVote_count(): String? {
        return vote_count
    }


    override fun toString(): String {
        return "overview = $overview\n original_language = $original_language\n, original_title = $original_title\n, video = $video\n, title = $title\n, genre_ids = $genre_ids\n, poster_path = $poster_path\n, backdrop_path = $backdrop_path\n, release_date = $release_date\n, popularity = $popularity\n, vote_average = $vote_average\n, id = $id\n, adult = $adult\n, vote_count = $vote_count\n\n\n"
    }
}