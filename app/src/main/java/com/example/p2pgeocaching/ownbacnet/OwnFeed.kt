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
        var file = File(feedName)
        file.createNewFile()


    }

    /**
     * Returns the [Publisher] as an [OwnPublisher] object (which it always is).
     */
    fun getOwnPublisher(): OwnPublisher {
        return publisher as OwnPublisher
    }
}