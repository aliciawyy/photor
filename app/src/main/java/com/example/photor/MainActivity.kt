package com.example.photor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.photor.data.PhotoRepository

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    PhotoRepository.initialize()
    setContentView(R.layout.activity_main)
  }
}