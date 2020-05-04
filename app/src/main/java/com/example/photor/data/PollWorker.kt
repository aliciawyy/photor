package com.example.photor.data

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.photor.MainActivity
import com.example.photor.NOTIFICATION_CHANNEL_ID
import com.example.photor.R
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

            val pendingIntent = PendingIntent.getActivity(
                context, 0, MainActivity.newIntent(context), 0)

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(context.getString(R.string.new_picture_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(context.getString(R.string.new_picture_title))
                .setContentText(context.getString(R.string.new_picture_text, query))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(0, notification)
        }
        return Result.success()
    }
}