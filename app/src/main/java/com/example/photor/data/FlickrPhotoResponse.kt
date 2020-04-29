package com.example.photor.data

import com.google.gson.annotations.SerializedName

class FlickrPhotoResponse {
    @SerializedName("photo")
    lateinit var photoItems: List<FlickrPhotoItem>
}