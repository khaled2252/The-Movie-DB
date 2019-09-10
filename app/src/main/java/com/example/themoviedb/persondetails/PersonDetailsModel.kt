package com.example.themoviedb.persondetails

class PersonDetailsModel() {
    private lateinit var personDetailsController: PersonDetailsController
    fun setPersonDetailsController(personDetailsController: PersonDetailsController) {
        this.personDetailsController= personDetailsController
    }
}