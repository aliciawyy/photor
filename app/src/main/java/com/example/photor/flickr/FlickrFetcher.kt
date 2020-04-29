package com.example.photor.flickr

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val FLICKR_URL = "https://www.flickr.com"
private const val TAG = "FlickrFetcher"

class FlickrFetcher {
    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(FLICKR_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchContent() {
        val flickrCall = flickrApi.fetchContents()
        // Execute the web request represented by the call object (flickrCall here)
        // enqueue(...) is executed in a background thread
        flickrCall.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "failed to fetch the photos: $t")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "Response received ${response.body()}")
            }
        })
    }
}