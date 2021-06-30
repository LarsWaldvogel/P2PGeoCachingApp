package com.example.p2pgeocaching.ownbacnet

import android.util.Log
import com.example.p2pgeocaching.activities.MainActivity
import java.io.File

/**
 * This is a [Feed] object, owned by the user, meaning it can be modified.
 */
class OwnFeed(entries: List<Entry>, ownPublisher: OwnPublisher) : Feed(entries, ownPublisher) {

    // TODO
    companion object {
        const val TAG = "OwnFeed"
    }

    fun createOwnFeed() {
        var str = ""
        val name = getOwnPublisher().name
        val salt = getOwnPublisher().getSalt()
        val feedName = name.plus("#").plus(salt)
        Log.d(TAG, "feedName = $feedName")
        // TODO* context?
        val file = File(feedName)
        // TODO* activate it
        //file.createNewFile()
    }

    //*
    fun createNewFeed(oldusername: String, key:String) {
        var str = ""
        val name = getOwnPublisher().name
        val salt = getOwnPublisher().getSalt()
        val feedName = name.plus("#").plus(salt)
        Log.d(TAG, "feedName = $feedName")
        // TODO* context?
        val file = File(feedName)

        val oldFeedName = oldusername.plus("#").plus(getOwnPublisher().getSaltOfOldPublisher(key))
        val oldFile = File(oldFeedName)
        oldFile.delete()
        file.createNewFile()
    }

    // TODO addEntry()
    /**
     * Returns the [Publisher] as an [OwnPublisher] object (which it always is).
     */
    fun getOwnPublisher(): OwnPublisher {
        return publisher as OwnPublisher
    }
}