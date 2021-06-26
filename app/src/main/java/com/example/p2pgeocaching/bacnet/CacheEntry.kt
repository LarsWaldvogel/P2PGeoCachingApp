package com.example.p2pgeocaching.bacnet

import com.example.p2pgeocaching.caches.OwnCache

/**
 * This subclass of Entry represents a Cache in the BaCNet-Feed.
 */
class CacheEntry(
    timestamp: Long,
    id: Int,
    signedPreviousID: String,
    content: String,
    signature: String
) : Entry(timestamp, id, signedPreviousID, CACHE_ENTRY, content, signature) {

    companion object {
        const val CACHE_ENTRY = "CacheEntry"

        /**
         * This method lets us create a CacheEntry with an [ownCache] object.
         * It also needs a [ownFeed] to determine the current
         */
        fun newCacheEntry(ownCache: OwnCache, ownFeed: OwnFeed) : CacheEntry {
            val timestamp = System.currentTimeMillis()
            val id = TODO()
            val signedPreviousID = TODO()
            // TODO: initialize things and construct CacheEntry
            return TODO()
        }
    }


}
