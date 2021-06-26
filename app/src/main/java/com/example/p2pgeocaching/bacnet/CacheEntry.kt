package com.example.p2pgeocaching.bacnet

import com.example.p2pgeocaching.caches.OwnCache

/**
 * This subclass of [Entry] represents a [Cache] in the BaCNet-Feed.
 * Its content is a serialized [Cache] object.
 */
class CacheEntry(
    timestamp: Long,
    id: Int,
    signedPreviousSignature: String,
    content: String,
    signature: String
) : Entry(timestamp, id, signedPreviousSignature, CACHE_ENTRY, content, signature) {

    companion object {

        /**
         * This method lets us create a CacheEntry with an [ownCache] object.
         * It also needs a [ownFeed] to determine the current position in the feed
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
