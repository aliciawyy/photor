package com.example.photor.data

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import retrofit2.http.Url
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0


class ThumbnailDownloader<in T> (
    private val responseHandler: Handler,
    private val onThumbnailDownloader: (T, Bitmap) -> Unit
) : HandlerThread(TAG) {

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()

    val fragmentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setUp() {
            Log.d(TAG, "Set up the ThumbnailDownloader thread.")
            start()
            looper
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            Log.d(TAG, "Tear down the ThumbnailDownloader thread.")
            quit()
        }
    }

    val viewLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearRequests() {
            Log.d(TAG, "Clear all requests.")
            responseHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val obj = msg.obj as T
                    handleRequest(obj)
                }
            }
        }
    }

    private fun handleRequest(obj: T) {
        val url = requestMap[obj] ?: return
        val bitmap = PhotoRepository.get().fetchPhoto(url) ?: return
        responseHandler.post(Runnable {
            if (requestMap[obj] != url || hasQuit) {
                return@Runnable
            }
            requestMap.remove(obj)
            onThumbnailDownloader(obj, bitmap)
        })
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(obj: T, @Url url: String) {
        Log.d(TAG, "Got an url = $url")
        requestMap[obj] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, obj).sendToTarget()
    }
}