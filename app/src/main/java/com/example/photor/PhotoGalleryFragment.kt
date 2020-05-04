package com.example.photor

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.photor.data.FlickrPhotoItem
import com.example.photor.data.PollWorker
import com.squareup.picasso.Picasso
import timber.log.Timber

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private val photoViewModel: PhotoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workRequest: WorkRequest = OneTimeWorkRequest.Builder(PollWorker::class.java)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workRequest)
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
        photoViewModel.photoList.observe(
            viewLifecycleOwner,
            Observer { photoList ->
                run {
                    photoRecyclerView.adapter = PhotoAdapter(photoList)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)
        val searchView = menu.findItem(R.id.menu_item_search).actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Timber.d("onQueryTextSubmit: query = $query")
                    if (query != null) {
                        this@PhotoGalleryFragment.photoViewModel.searchPhotos(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Timber.d("onQueryTextChange: newText = $newText")
                    return false
                }
            })

            setOnSearchClickListener {
                searchView.setQuery(photoViewModel.searchQueryText, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoViewModel.searchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

