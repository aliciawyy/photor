package com.example.photor

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photor.data.FlickrPhotoItem
import com.example.photor.data.PhotoRepository

class PhotoViewModel(): ViewModel() {

    val photoList: LiveData<List<FlickrPhotoItem>> = PhotoRepository.get().fetchPhotos()
}