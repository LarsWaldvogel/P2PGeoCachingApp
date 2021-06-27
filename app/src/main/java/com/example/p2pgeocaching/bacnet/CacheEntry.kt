package com.example.p2pgeocaching.bacnet

import com.example.p2pgeocaching.caches.OwnCache
import com.example.p2pgeocaching.constants.Constants.Companion.CACHE_ENTRY
import com.example.p2pgeocaching.data.Serializer

/**
 * This subclass of [Entry] represents a Cache in the BaCNet-Feed.
 * Its content is a serialized Cache object.
 */
class CacheEntry(
    timestamp: Long,
    id: Int,
    signedPreviousSignature: String,
    content: String,
    signature: String
) : Entry(timestamp, id, signedPreviousSignature, CACHE_ENTRY, content, signature) {

    // TODO

    companion object {

        /**
         * This method lets us create a CacheEntry with an [ownCache] object.
         * It also needs a [ownFeed] to determine the current position in the feed.
         */
        fun newCacheEntry(ownCache: OwnCache, ownFeed: OwnFeed): CacheEntry {
            val timestamp = System.currentTimeMillis()
            val id = ownFeed.getNextID()
            val previousSignature = ownFeed.getLastSignature()
            val signedPreviousSignature = ownFeed.getOwnPublisher().sign(previousSignature)

            // It is important we concatenate the type as well, to get the correct signature
            val type = CACHE_ENTRY
            val content = Serializer.serializeCacheToString(ownCache)

            val concatenatedString: String =
                timestamp.toString() + id.toString() + signedPreviousSignature + type + content
            val hashString = concatenatedString.hashCode().toString()
            val signature = ownFeed.getOwnPublisher().sign(hashString)
            return CacheEntry(timestamp, id, signedPreviousSignature, content, signature)
        }
    }


}
