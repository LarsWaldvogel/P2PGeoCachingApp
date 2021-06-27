package com.example.p2pgeocaching.bacnet

import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.constants.Constants.Companion.HOF_ENTRY

/**
 * This [Entry] subclass is created when the [Feed] owner solves a [Cache].
 * Its content represents the encrypted name of the solver.
 */
class HoFEntry(
    timestamp: Long,
    id: Int,
    signedPreviousSignature: String,
    content: String,
    signature: String
) : Entry(timestamp, id, signedPreviousSignature, content, HOF_ENTRY, signature) {

    // TODO

    companion object {

        /**
         * This method lets us create a HoFEntry with a [privateKey] and a [Cache] object
         * It also needs a [ownFeed] to determine the current position in the feed.
         */
        fun newHoFEntry(privateKey: String, cache: Cache, ownFeed: OwnFeed): HoFEntry {
            val timestamp = System.currentTimeMillis()
            val id = TODO()
            val signedPreviousID = TODO()
            // TODO: initialize things and construct HoFEntry
            return TODO()
        }
    }
}