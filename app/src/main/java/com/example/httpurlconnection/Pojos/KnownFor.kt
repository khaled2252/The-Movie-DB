package com.example.httpurlconnection.Pojos

class Known_for {
    var overview: String? = null

    var original_language: String? = null

    var original_title: String? = null

    var video: String? = null

    var title: String? = null

    var genre_ids: Array<String>? = null

    var poster_path: String? = null

    var backdrop_path: String? = null

    var media_type: String? = null

    var release_date: String? = null

    var vote_average: String? = null

    var id: String? = null

    var adult: String? = null

    var vote_count: String? = null

    override fun toString(): String {
        return "ClassPojo [overview = $overview, original_language = $original_language, original_title = $original_title, video = $video, title = $title, genre_ids = $genre_ids, poster_path = $poster_path, backdrop_path = $backdrop_path, media_type = $media_type, release_date = $release_date, vote_average = $vote_average, id = $id, adult = $adult, vote_count = $vote_count]"
    }
}