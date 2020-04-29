package com.example.photor.data

import com.google.gson.annotations.SerializedName

data class FlickrPhotoItem (
    var id: String = "",
    var title: String = "",
    @SerializedName("url_s") var url: String = ""
)