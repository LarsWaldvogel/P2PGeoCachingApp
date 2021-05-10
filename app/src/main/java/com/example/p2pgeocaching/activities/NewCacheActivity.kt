package com.example.p2pgeocaching.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.caches.CacheList
import com.example.p2pgeocaching.caches.OwnCache
import com.example.p2pgeocaching.data.Serializer
import com.example.p2pgeocaching.databinding.ActivityNewCacheBinding
import com.example.p2pgeocaching.inputValidator.InputValidator
import com.example.p2pgeocaching.p2pexceptions.InputIsEmptyException
import java.io.File

class NewCacheActivity: AppCompatActivity() {

    companion object {
        const val TAG = "NewCacheActivity"
    }

    private lateinit var binding: ActivityNewCacheBinding
    private lateinit var cacheList: CacheList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "New Cache"

        binding = ActivityNewCacheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage and takes the object out of it
        val context = applicationContext
        val cacheListFile = File(context.filesDir, MainActivity.CACHE_LIST_FILE)
        val userNameFile = File(context.filesDir, MainActivity.U_NAME_FILE)

        cacheList = Serializer.deserializeCacheList(cacheListFile)

        binding.saveCacheButtonText.setOnClickListener {
            try {
                // Throws StringContainsIllegalCharacterException if one of the inputs is not legal
                saveInputToCacheList(userNameFile, cacheListFile)
            } catch (e: Throwable) {
                Log.d(TAG, "Created cache contained illegal characters or was empty")
            }
        }
    }


    /**
     * This takes the input in the fields, creates a [OwnCache] with the parameters, adds it to the
     * [cacheList], then writes the [cacheList] to the [cacheListFile] by serializing it.
     * Also needs the [userNameFile] to get the creator's name.
     */
    private fun saveInputToCacheList(userNameFile: File, cacheListFile: File) {
        // Save input to variables
        val cacheTitle = binding.newCacheNameEditText.text.toString()
        val cacheDesc = binding.newCacheDescEditText.text.toString()
        val creatorString = userNameFile.readLines().toString()
        val creator = creatorString.substring(1, creatorString.length - 1)
        Log.d(TAG, "Title: $cacheTitle\nDesc: $cacheDesc\nCreator: $creator")

        // Validate input, throw exception if illegal
        if (cacheTitle == "" || cacheDesc == "") {
            throw InputIsEmptyException()
        }
        InputValidator.checkTextForIllegalCharacters(listOf(cacheTitle, cacheDesc))

        // Create the new Cache and add it to the cacheList
        val newCache = OwnCache(cacheTitle, cacheDesc, creator, this)
        cacheList.add(newCache)

        // Save the new list to file
        Serializer.serializeCacheList(cacheList, cacheListFile)
    }
}