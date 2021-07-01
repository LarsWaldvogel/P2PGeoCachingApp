package com.example.p2pgeocaching.ownbacnet

import com.example.p2pgeocaching.constants.Constants
import com.example.p2pgeocaching.constants.Constants.Companion.LOG_ENTRY
import com.example.p2pgeocaching.data.FeedDataParser
import com.example.p2pgeocaching.data.Serializer
import java.io.File

/**
 * This class is created whenever the app receives new data from another user.
 * Thanks to this, one could create a logical time line, making faking timestamps incredibly hard.
 * The [content] contains tuples of all the received non-log [Entry] [id]s and [signature]s,
 * with their respective [Publisher].
 * e. g. : "15, sdfh7Hjs89, Tom#2571; 17, asd7873HHGk, Caroline#3142; ..."
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
        fun newLogEntry(newEntries: List<Entry>, ownFeed: OwnFeed, context: File): LogEntry {
            val timestamp = System.currentTimeMillis()
            val id = ownFeed.getNextID()
            val previousSignature = ownFeed.getLastSignature()
            val signedPreviousSignature = ownFeed.getOwnPublisher().sign(previousSignature)
            val type = LOG_ENTRY
            // TODO: initialize things and construct HoFEntry
            var content = ""

            // TODO* CHECK: test if this works
            val fdp = FeedDataParser()
            val feedNamesFile = File(context, Constants.FEED_NAMES_FILE)
            val feedNameContent = feedNamesFile.readText()
            val feedNameList = feedNameContent.split("\n")
            var bool = false
            for (entry in newEntries) {
                bool = false
                for (feedName in feedNameList) {
                    val feedFile = File(context, feedName)
                    val listOfEntries = fdp.feedToEntrylist(feedFile)
                    for (feedEntry in listOfEntries) {
                        if (entry.equals(feedEntry)) {
                            content = content.plus(entry.id).plus(",").plus(entry.signature).plus(",").plus(feedName).plus(";")
                            bool = true
                            break
                        }
                    }
                    if (bool) {
                        break
                    }
                }
            }
            val concatenatedString: String =
                timestamp.toString() + id.toString() + signedPreviousSignature + type + content
            val hashString = concatenatedString.hashCode().toString()
            val signature = ownFeed.getOwnPublisher().sign(hashString)
            return LogEntry(timestamp, id, signedPreviousSignature, content, signature)
        }
    }
}