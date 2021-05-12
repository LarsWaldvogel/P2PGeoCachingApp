package com.example.p2pgeocaching.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.databinding.ActivitySolvedCacheDetailBinding

/**
 * This class is used when viewing the details of a Cache
 */
class SolvedCacheDetailActivity : AppCompatActivity() {

    companion object {
        const val CACHE = "cache"
    }

    private lateinit var binding: ActivitySolvedCacheDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivitySolvedCacheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if a CacheData object was given
        // If no CachedData object was given, return to previous activity
        val cacheData: CacheData = intent?.extras?.getSerializable(CACHE) as CacheData

        // If a cache was given, parse it to
        val cache = CacheDataParser.dataToCache(cacheData)

        // Initialize the fields of the UI
        title = getString(R.string.solved_cache_title)
        binding.cacheTitle.text = cache.title
        binding.cacheDesc.text = cache.desc
        binding.creator.text = getString(R.string.creator_text, cache.creator)
        binding.hallOfFameText.text = cache.plainTextHOF
        // TODO:
        //  if it is a ownCache: show privateKey when asked
        //  if it is a unsolvedCache: show button to solve cache, which leads to own activity
        //  if it is a solvedCache: show a "Solved!" text




    }

}