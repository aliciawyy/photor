package com.example.photor.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "PhotoRepository"
private const val FLICKR_URL = "https://api.flickr.com"


class PhotoRepository private constructor() {

    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(FLICKR_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos() : LiveData<List<FlickrPhotoItem>> {
        val flickrCall = flickrApi.fetchPhotos()
        val responseLiveData = MutableLiveData<List<FlickrPhotoItem>>()
        // Execute the web request represented by the call object (flickrCall here)
        // enqueue(...) is executed in a background thread
        flickrCall.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch the photos: $t")
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received.")
                responseLiveData.value = response.body()?.photos?.photoItems?.filterNot {
                    it.url.isBlank() } ?: emptyList()
            }
        })
        return responseLiveData
    }

    companion object {
        private var instance: PhotoRepository? = null

        fun initialize() {
            if (instance == null) {
                synchronized(this) {
                    instance = PhotoRepository()
                }
            }
        }

        fun get() : PhotoRepository = instance!!
    }
}