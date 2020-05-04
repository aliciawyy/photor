package com.example.photor.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<FlickrResponse>

    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse>

    @GET
    fun fetchBytesFromUrl(@Url url: String): Call<ResponseBody>
}