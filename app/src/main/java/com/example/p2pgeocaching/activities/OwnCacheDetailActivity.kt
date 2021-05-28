package com.example.p2pgeocaching.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.caches.CacheList
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.data.Serializer
import com.example.p2pgeocaching.databinding.ActivityOwnCacheDetailBinding
import java.io.File


/**
 * This class is used when viewing the details of a Cache
 */
class OwnCacheDetailActivity : AppCompatActivity() {

    companion object {
        const val CACHE = "cache"
        const val PRIVATE_KEY = "private key"
        const val CACHE_LIST_FILE = "cacheList"
    }

    private lateinit var binding: ActivityOwnCacheDetailBinding
    private lateinit var cache: Cache
    private lateinit var cacheList: CacheList
    private lateinit var context: Context
    private lateinit var cacheListFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivityOwnCacheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        context = applicationContext
        cacheListFile = File(context.filesDir, CACHE_LIST_FILE)

        // Get cacheList
        cacheList = Serializer.deserializeCacheList(cacheListFile)


        // Check if a CacheData object was given
        // If no CachedData object was given, return to previous activity
        val cacheData: CacheData = intent?.extras?.getSerializable(CACHE) as CacheData

        // If a cache was given, parse it to
        cache = CacheDataParser.dataToCache(cacheData)

        // Initialize the fields of the UI
        title = getString(R.string.own_cache_title)
        binding.cacheTitle.text = cache.title
        binding.cacheDesc.text = cache.desc
        binding.creator.text = getString(R.string.creator_text, cache.creator)
        binding.hallOfFameText.text = cache.plainTextHOF

        // Press the key button to get to the private key
        binding.viewPrivateKeyButton.setOnClickListener {
            context = applicationContext
            val intent = Intent(context, PrivateKeyActivity::class.java)
            intent.putExtra(PRIVATE_KEY, cache.prvKey)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)
        }

        // Press the delete button to return to the list with the cache removed
        binding.deleteButtonOwn.setOnClickListener {
            cacheList.removeCacheByID(cache.id)
            Serializer.serializeCacheList(cacheList, cacheListFile)
            finish()
        }
    }

}