package com.example.photor.data

import android.net.Uri
import com.google.gson.annotations.SerializedName

private const val FLICKR_BASE_URL = "https://www.flickr.com/photos/"

data class FlickrPhotoItem (
    var id: String = "",
    var title: String = "",
    @SerializedName("url_s") var url: String = "",
    var owner: String = ""
) {
    val photoPageUri : Uri
    get() = Uri.parse(FLICKR_BASE_URL)
        .buildUpon()
        .appendPath(owner)
        .appendPath(id)
        .build()
}