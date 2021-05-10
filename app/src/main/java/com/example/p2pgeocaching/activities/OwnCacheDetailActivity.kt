package com.example.p2pgeocaching.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.databinding.ActivityOwnCacheDetailBinding

/**
 * This class is used when viewing the details of a Cache
 */
class OwnCacheDetailActivity : AppCompatActivity() {

    companion object {
        const val CACHE = "cache"
    }

    private lateinit var binding: ActivityOwnCacheDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivityOwnCacheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if a CacheData object was given
        // If no CachedData object was given, return to previous activity
        val cacheData: CacheData = intent?.extras?.getSerializable(CACHE) as CacheData

        // If a cache was given, parse it to
        val cache = CacheDataParser.dataToCache(cacheData)
        // TODO: initialize the fields in the UI
        //  Contents: Show title, desc, creator, plainTextHOF
        //  if it is a ownCache: show privateKey when asked
        //  if it is a unsolvedCache: show button to solve cache, which leads to own activity
        //  if it is a solvedCache: show a "Solved!" text




    }

}