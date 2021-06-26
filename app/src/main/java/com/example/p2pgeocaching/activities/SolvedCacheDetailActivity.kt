package com.example.p2pgeocaching.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.caches.CacheList
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.data.Serializer
import com.example.p2pgeocaching.databinding.ActivitySolvedCacheDetailBinding
import java.io.File

/**
 * This class is used when viewing the details of a Cache
 */
class SolvedCacheDetailActivity : AppCompatActivity() {

    companion object {
        const val CACHE = "cache"
        const val CACHE_LIST_FILE = "cacheList"
    }

    private lateinit var binding: ActivitySolvedCacheDetailBinding
    private lateinit var cache: Cache
    private lateinit var cacheList: CacheList
    private lateinit var context: Context
    private lateinit var cacheListFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivitySolvedCacheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        context = applicationContext
        cacheListFile = File(context.filesDir, CACHE_LIST_FILE)

        // Get cacheList
        cacheList = Serializer.deserializeCacheList(cacheListFile)

        // Check if a CacheData object was given
        // If no CachedData object was given, return to previous activity
        val bundleData = intent?.extras?.getSerializable(OwnCacheDetailActivity.CACHE)
        if (bundleData == null) {
            Log.d(OwnCacheDetailActivity.TAG, "Intent did not contain Cache")
            finish()
        }
        val cacheData: CacheData = bundleData as CacheData

        // If a cache was given, parse it to
        cache = CacheDataParser.dataToCache(cacheData)

        // Initialize the fields of the UI
        title = getString(R.string.solved_cache_title)
        binding.cacheTitle.text = cache.title
        binding.cacheDesc.text = cache.desc
        binding.creator.text = getString(R.string.creator_text, cache.creator)
        binding.hallOfFameText.text = cache.plainTextHOF

        // Press the delete button to return to the list with the cache removed
        binding.deleteButtonSolved.setOnClickListener {
            cacheList.removeCacheByID(cache.id)
            Serializer.serializeCacheList(cacheList, cacheListFile)
            finish()
        }
    }
}