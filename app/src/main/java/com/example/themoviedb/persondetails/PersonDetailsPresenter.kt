package com.example.themoviedb.persondetails

import com.example.themoviedb.pojos.PersonImages
import org.json.JSONObject

class PersonDetailsPresenter(private val view: PersonDetailsView,private val model: PersonDetailsModel) {

    internal var resultList = ArrayList<PersonImages?>()

    private fun loadProfiles(isDataFetched: (Boolean) -> Unit) {
        model.fetchJson(view.getProfileId()) {
            isDataFetched(true)
            if (!it.isNullOrEmpty()) {
                val jsonArrayOfProfiles = JSONObject(it).getJSONArray("profiles")

                for (i in 0 until jsonArrayOfProfiles.length()) {
                    val personImages = PersonImages()
                    personImages.personId = JSONObject(it).getString("id")
                    personImages.filePath =
                        jsonArrayOfProfiles.getJSONObject(i).getString("file_path")
                    resultList.add(personImages)
                }
                view.notifyItemRangeChangedInRecyclerView(jsonArrayOfProfiles.length())
            }

        }
    }

    fun loadImage(path: String?, bitmap: (Any?) -> Unit) {
        model.fetchImage(path!!) {
            bitmap(it)
        }
    }

    fun viewOnCreated() {
        view.setUiFromIntent()
        loadProfiles{}
        }

    fun itemViewOnClick(arr: Array<Any>) {
        model.saveImage(arr)
        view.navigateToImageActivity()
    }
}
