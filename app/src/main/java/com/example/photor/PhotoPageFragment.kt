package com.example.photor

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

private const val PHOTO_PAGE_URL = "PhotoPageUrl"

class PhotoPageFragment : VisibleFragment() {

    private lateinit var photoPageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoPageUri = arguments?.getParcelable(PHOTO_PAGE_URL) ?: Uri.EMPTY
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_page, container, false)
        val webView: WebView = view.findViewById(R.id.photo_web_view)
        val progressBar: ProgressBar = view.findViewById(R.id.photo_page_progress_bar)
        progressBar.max = 100

        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                (activity as AppCompatActivity).supportActionBar?.subtitle = title
            }
        }
        webView.loadUrl(photoPageUri.toString())

        return view
    }

    companion object {
        fun newInstance(photoPageUri: Uri): PhotoPageFragment = PhotoPageFragment().apply {
            arguments = Bundle().apply {
                putParcelable(PHOTO_PAGE_URL, photoPageUri)
            }
        }
    }
}
