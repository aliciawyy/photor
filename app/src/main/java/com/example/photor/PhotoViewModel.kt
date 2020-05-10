package com.example.photor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.photor.data.FlickrPhotoItem
import com.example.photor.data.PhotoRepository
import com.example.photor.data.PhotorPreferences
import timber.log.Timber

class PhotoViewModel(private val app: Application) : AndroidViewModel(app) {

    val photoList: LiveData<List<FlickrPhotoItem>>
    private val queryText = MutableLiveData<String>()

    val searchQueryText
        get() = queryText.value ?: ""

    init {
        queryText.value = PhotorPreferences.getStoredQuery(app)
        photoList = Transformations.switchMap(queryText) {
            Timber.d("query text = $it")
            if (it.isBlank()) {
                PhotoRepository.get().fetchPhotos()
            } else {
                PhotoRepository.get().searchPhotos(it)
            }
        }
    }

    fun searchPhotos(query: String) {
        PhotorPreferences.setStoredQuery(app, query)
        queryText.value = query
    }
}
