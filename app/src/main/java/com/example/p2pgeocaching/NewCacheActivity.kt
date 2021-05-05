package com.example.p2pgeocaching

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.databinding.ActivityNewCacheBinding
import java.io.File

class NewCacheActivity: AppCompatActivity() {

    companion object {
        const val U_NAME = "u-name"
        const val CACHE_LIST_FILE = "cacheList"
        const val TAG = "NewCacheActivity"
    }

    private lateinit var binding: ActivityNewCacheBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "New Cache"

        binding = ActivityNewCacheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        val context = applicationContext
        val cacheListFile = File(context.filesDir, MainActivity.CACHE_LIST_FILE)


    }
}