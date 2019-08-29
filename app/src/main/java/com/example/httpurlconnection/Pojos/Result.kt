package com.example.httpurlconnection.Pojos

import java.io.Serializable

    class Result : Serializable {
    var gender: String? = null

    var known_for_department: String? = null

    var popularity: String? = null

    var known_for: Array<Known_for>? = null

    var name: String? = null

    var profile_path: String? = null

    var id: String? = null

    var adult: String? = null

    override fun toString(): String {
        return "ClassPojo [gender = $gender, known_for_department = $known_for_department, popularity = $popularity, known_for = $known_for, name = $name, profile_path = $profile_path, id = $id, adult = $adult]"
    }
}