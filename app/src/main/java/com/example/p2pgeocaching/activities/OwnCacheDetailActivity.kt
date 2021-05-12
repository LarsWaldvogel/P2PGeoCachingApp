package com.example.p2pgeocaching.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.databinding.ActivityOwnCacheDetailBinding

/**
 * This class is used when viewing the details of a Cache
 */
class OwnCacheDetailActivity : AppCompatActivity() {

    companion object {
        const val CACHE = "cache"
        const val PRIVATE_KEY = "private key"
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

        // Initialize the fields of the UI
        title = getString(R.string.own_cache_title)
        binding.cacheTitle.text = cache.title
        binding.cacheDesc.text = cache.desc
        binding.creator.text = getString(R.string.creator_text, cache.creator)
        binding.hallOfFameText.text = cache.plainTextHOF

        // Press the button to get to the private key
        binding.viewPrivateKeyButton.setOnClickListener {
            val context = applicationContext
            val intent = Intent(context, PrivateKeyActivity::class.java)
            intent.putExtra(PRIVATE_KEY, cache.prvKey)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)
        }
    }

}