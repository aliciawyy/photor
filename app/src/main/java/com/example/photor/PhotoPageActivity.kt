package com.example.photor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PhotoPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)
        if (supportFragmentManager.findFragmentById(R.id.photo_page_fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.photo_page_fragment_container, PhotoPageFragment.newInstance(intent.data!!))
                .commit()
        }
    }

    companion object {
        fun newIntent(context: Context, photoPageUri: Uri): Intent =
            Intent(context, PhotoPageActivity::class.java).apply {
                data = photoPageUri
            }
    }
}
