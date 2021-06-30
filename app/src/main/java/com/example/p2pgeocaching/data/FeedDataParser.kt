package com.example.p2pgeocaching.data

import com.example.p2pgeocaching.ownbacnet.CacheEntry
import com.example.p2pgeocaching.ownbacnet.Entry
import java.io.File

/**
 * This class parses a FeedData object to an actual Feed and back.
 */
class FeedDataParser {

    fun feedToEntrylist(feedFile : File): List<Entry> {
        if(feedFile.length() == 0L) {
            return mutableListOf()
        } else {
            val content = feedFile.readText()
            val entrylist = content.split("-*-*-")
            val listOfEntries = mutableListOf<Entry>()
            for (item in entrylist) {
                val ep = item.split("***")
                val entry = Entry(
                    ep[0].toLong(),// timestamp
                    ep[1].toInt(), // id
                    ep[2],         // signedPreviousSignature
                    ep[3],         // content
                    ep[4],         // type
                    ep[5]          // signature
                )
                listOfEntries.add(entry)
            }
            return listOfEntries
        }
    }

    fun appendCacheToFeed(cacheEntry : CacheEntry) : String {
        val str =
            cacheEntry.timestamp.toString()
                .plus("\n***")
                .plus("\n"+cacheEntry.id).plus("\n***")
                .plus("\n"+cacheEntry.signedPreviousSignature).plus("\n***")
                .plus("\n"+cacheEntry.content).plus("\n***")
                .plus("\n"+cacheEntry.type).plus("\n***")
                .plus("\n"+cacheEntry.signature)
        return str
    }

}