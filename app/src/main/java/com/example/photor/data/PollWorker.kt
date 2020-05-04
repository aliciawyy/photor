package com.example.photor.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class PollWorker(private val context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters){
    override fun doWork(): Result {
        Timber.d("called doWork")
        val query = PhotorPreferences.getStoredQuery(context)

        val flickrRequest = if (query.isEmpty()) {
            PhotoRepository.get().fetchPhotosRequest()
        } else {
            PhotoRepository.get().searchPhotosRequest(query)
        }

        val photoItems: List<FlickrPhotoItem> = flickrRequest.execute().body()?.photos?.photoItems
            ?: return Result.success()
        val lastResultId = PhotorPreferences.getLastResultId(context)
        val currentLastResultId = photoItems.last().id
        if (currentLastResultId == lastResultId) {
            Timber.d("nothing new.")
        } else {
            Timber.d("Got a new result: $currentLastResultId")
            PhotorPreferences.setLastResultId(context, currentLastResultId)
        }
        return Result.success()
    }
}