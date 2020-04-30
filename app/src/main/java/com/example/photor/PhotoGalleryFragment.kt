package com.example.photor

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photor.data.FlickrPhotoItem
import com.example.photor.data.ThumbnailDownloader

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private val photoViewModel: PhotoViewModel by activityViewModels()
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thumbnailDownloader = ThumbnailDownloader(Handler()) {
            photoHolder, bitmap -> photoHolder.bindImage(BitmapDrawable(resources, bitmap))
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
        photoViewModel.photoList.observe(
            viewLifecycleOwner,
            Observer { photoList ->
                run {
                    photoRecyclerView.adapter = PhotoAdapter(photoList)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    private class PhotoHolder(photoImageView: ImageView) : RecyclerView.ViewHolder(photoImageView) {
        val bindImage: (Drawable) -> Unit = photoImageView::setImageDrawable
    }

    private inner class PhotoAdapter(private val photoList: List<FlickrPhotoItem>):
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_photo_item, parent, false) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = photoList.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val photoItem = photoList[position]
            thumbnailDownloader.queueThumbnail(holder, photoItem.url)
        }

    }
}

