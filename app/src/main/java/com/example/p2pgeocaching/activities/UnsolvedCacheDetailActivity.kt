package com.example.p2pgeocaching.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.caches.CacheList
import com.example.p2pgeocaching.caches.SolvedCache
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.data.Serializer
import com.example.p2pgeocaching.databinding.ActivityUnsolvedCacheDetailBinding
import java.io.File

/**
 * This class is used when viewing the details of a Cache
 */
class UnsolvedCacheDetailActivity : AppCompatActivity() {

    companion object {
        const val CACHE = "cache"
        const val PUBLIC_KEY = "public key"
        const val ID = "id"
        const val CACHE_LIST_FILE = "cacheList"
    }

    private lateinit var binding: ActivityUnsolvedCacheDetailBinding
    private lateinit var cache: Cache
    private lateinit var cacheList: CacheList
    private lateinit var cacheListFile: File
    private lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivityUnsolvedCacheDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if a CacheData object was given
        // If no CachedData object was given, return to previous activity
        val cacheData: CacheData = intent?.extras?.getSerializable(CACHE) as CacheData

        // If a cache was given, parse it to
        cache = CacheDataParser.dataToCache(cacheData)

        // Open cacheList to check if it is still in there, if not, leave activity
        context = applicationContext
        cacheListFile = File(context.filesDir, SolveActivity.CACHE_LIST_FILE)
        cacheList = Serializer.deserializeCacheList(cacheListFile)
        if (cacheList.findByID(cache.id) == null) {
            finish()
        }

        // Initialize the fields of the UI
        title = getString(R.string.unsolved_cache_title)
        binding.cacheTitle.text = cache.title
        binding.cacheDesc.text = cache.desc
        binding.creator.text = getString(R.string.creator_text, cache.creator)
        binding.hallOfFameText.text = cache.plainTextHOF

        // If solve button is clicked, open SolveActivity
        binding.solveCacheButton.setOnClickListener {
            val intent = Intent(context, SolveActivity::class.java)
            intent.putExtra(PUBLIC_KEY, cache.pubKey)
            intent.putExtra(ID, cache.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)
        }

        // Press the delete button to return to the list with the cache removed
        binding.deleteButtonUnsolved.setOnClickListener {
            cacheList.removeCacheByID(cache.id)
            Serializer.serializeCacheList(cacheList, cacheListFile)
            finish()
        }
    }


    override fun onRestart() {
        super.onRestart()

        // Check if a CacheData object was given
        // If no CachedData object was given, return to previous activity
        val cacheData: CacheData = intent?.extras?.getSerializable(CACHE) as CacheData

        // If a cache was given, parse it to
        val cache = CacheDataParser.dataToCache(cacheData)

        // Open cacheList to check if it has been solved, if yes, leave activity
        cacheListFile = File(context.filesDir, SolveActivity.CACHE_LIST_FILE)
        val cacheList = Serializer.deserializeCacheList(cacheListFile)
        if (cacheList.findByID(cache.id) is SolvedCache) {
            finish()
        }
    }
}