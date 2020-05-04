package com.example.photor.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Url

private const val TAG = "PhotoRepository"
private const val FLICKR_URL = "https://api.flickr.com"


class PhotoRepository private constructor() {

    private val flickrApi: FlickrApi

    init {
        val client = OkHttpClient.Builder()
          .addInterceptor(FlickrInterceptor())
          .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(FLICKR_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos() : LiveData<List<FlickrPhotoItem>> = getPhotos(flickrApi.fetchPhotos())

    fun searchPhotos(query: String): LiveData<List<FlickrPhotoItem>> =
      getPhotos(flickrApi.searchPhotos(query))

    private fun getPhotos(flickrCall: Call<FlickrResponse>): LiveData<List<FlickrPhotoItem>> {
        val responseLiveData = MutableLiveData<List<FlickrPhotoItem>>()
        // Execute the web request represented by the call object (flickrCall here)
        // enqueue(...) is executed in a background thread
        flickrCall.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch the photos: $t")
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received = ${response.body()}")
                responseLiveData.value = response.body()?.photos?.photoItems?.filterNot {
                    it.url.isBlank() } ?: emptyList()
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchPhoto(@Url url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchBytesFromUrl(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.d(TAG, "Decoded bitmap=$bitmap from Response=$response")
        return bitmap
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