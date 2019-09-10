package com.example.themoviedb.persondetails

class PersonDetailsController(val detailsActivityActivity: PersonDetailsActivity, val model: PersonDetailsModel) {

    init {
        model.setPersonDetailsController(this)
    }
}