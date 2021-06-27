package com.example.p2pgeocaching.bacnet

import com.example.p2pgeocaching.constants.Constants.Companion.LOG_ENTRY

/**
 * This class is created whenever the app receives new data from another user.
 * Thanks to this, one could create a logical time line, making faking timestamps incredibly hard.
 * The [content] contains tuples of all the received non-log [Entry] [id]s and [signature]s,
 * with their respective [Publisher].
 * e. g. : "15, sdfh7Hjs89, Tom; 17, asd7873HHGk, Caroline; ..."
 */
class LogEntry(
    timestamp: Long,
    id: Int,
    signedPreviousSignature: String,
    content: String,
    signature: String
) : Entry(timestamp, id, signedPreviousSignature, content, LOG_ENTRY, signature) {

    // TODO

    companion object {

        /**
         * This method lets us create a [LogEntry] with a list of [newEntries].
         * It also needs a [ownFeed] to determine the current position in the feed.
         */
        fun newLogEntry(newEntries: List<Entry>, ownFeed: OwnFeed): LogEntry {
            val timestamp = System.currentTimeMillis()
            val id = TODO()
            val signedPreviousID = TODO()
            // TODO: initialize things and construct HoFEntry
            return TODO()
        }
    }
}