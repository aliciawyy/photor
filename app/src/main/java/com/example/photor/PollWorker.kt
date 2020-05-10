package com.example.photor

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.photor.data.FlickrPhotoItem
import com.example.photor.data.PhotoRepository
import com.example.photor.data.PhotorPreferences
import timber.log.Timber

class PollWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val query =
            PhotorPreferences.getStoredQuery(context)

        val flickrRequest = if (query.isEmpty()) {
            PhotoRepository.get().fetchPhotosRequest()
        } else {
            PhotoRepository.get().searchPhotosRequest(query)
        }

        val photoItems: List<FlickrPhotoItem> = flickrRequest.execute().body()?.photos?.photoItems
            ?: return Result.success()
        val lastResultId =
            PhotorPreferences.getLastResultId(context)
        val currentLastResultId = photoItems.first().id
        if (currentLastResultId == lastResultId) {
            Timber.d("nothing new.")
        } else {
            Timber.d("Got a new result: $currentLastResultId")
            PhotorPreferences.setLastResultId(
                context,
                currentLastResultId
            )

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

            val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
                putExtra(NOTIFICATION, notification)
                putExtra(REQUEST_CODE, 0)
            }
            context.sendBroadcast(intent, PERM_PRIVATE)
        }
        return Result.success()
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "com.example.photor.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.example.photor.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}
