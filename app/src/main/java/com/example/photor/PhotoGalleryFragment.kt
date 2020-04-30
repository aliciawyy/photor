package com.example.photor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photor.data.FlickrPhotoItem
import com.squareup.picasso.Picasso

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private val photoViewModel: PhotoViewModel by activityViewModels()

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
        photoViewModel.photoList.observe(
            viewLifecycleOwner,
            Observer { photoList ->
                run {
                    photoRecyclerView.adapter = PhotoAdapter(photoList)
                }
            }
        )
    }

    private class PhotoHolder(private val photoImageView: ImageView)
        : RecyclerView.ViewHolder(photoImageView) {
        fun bindPhoto(photoItem: FlickrPhotoItem) {
            Picasso.get().load(photoItem.url)
                .into(photoImageView)
        }
    }

    private class PhotoAdapter(private val photoList: List<FlickrPhotoItem>):
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_photo_item, parent, false) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = photoList.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bindPhoto(photoList[position])
        }
    }
}

