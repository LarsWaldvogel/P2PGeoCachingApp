package com.example.p2pgeocaching.data

import android.provider.SyncStateContract
import android.util.Log
import com.example.p2pgeocaching.activities.NewCacheActivity
import com.example.p2pgeocaching.caches.CacheListGenerator
import com.example.p2pgeocaching.constants.Constants
import java.io.File
import java.io.Serializable
import com.example.p2pgeocaching.ownbacnet.Entry
import com.example.p2pgeocaching.ownbacnet.LogEntry
import com.example.p2pgeocaching.ownbacnet.OwnPublisher
import com.example.p2pgeocaching.ownbacnet.OwnFeed

/**
 * This class represents a feed in data form to be serialized and back.
 */
@kotlinx.serialization.Serializable
class FeedData : Serializable {

    companion object {
        const val TAG = "FeedData"
    }

    fun feedToData(context: File): String {
        val file = File(context, "file")
        if (!file.exists()) {
            file.createNewFile()
            Log.i(TAG, "Created File")
        }

        val userNameFile = File(context, Constants.U_NAME_FILE)
        var userName = userNameFile.readLines().toString()
        userName = userName.substring(1, userName.length - 1)
        var personData = File(context, Constants.PERSON_DATA)
        val content = personData.readText()
        val keys = content.split(" ")
        val pubkey = keys[0].split("_")
        val salt = pubkey[1].takeLast(4)
        val feedname = userName.plus("#").plus(salt)

        val ownFeedFile = File(context, feedname)
        val ownFeedContent = ownFeedFile.readText()

        if (ownFeedContent.length != 0) {
            file.appendText(feedname)
            file.appendText("_:_:_")
            file.appendText("".plus(ownFeedContent))
            Log.i(TAG, "File Content after adding own feed = "+file.readText())
        }

        var feedNamesFile = File(context, Constants.FEED_NAMES_FILE)
        var feedNameList = feedNamesFile.readText().split("\n")
        for (feed in feedNameList) {
            val feedFile = File(context, feed)
            val feedContent = feedFile.readText()
            if (file.length() == 0L && feedContent.length != 0) {
                file.appendText(feed)
                file.appendText("_:_:_")
                file.appendText("".plus(feedContent))
                Log.i(TAG, "File Content after adding new feed = "+file.readText())
            } else if (feedContent.length != 0) {
                file.appendText("#####")
                file.appendText("".plus(feed))
                file.appendText("_:_:_")
                file.appendText("".plus(feedContent))
                Log.i(TAG, "File Content after adding new feed = "+file.readText())
            }
        }
        Log.i(TAG, "File Content at the end = "+file.readText())
        return "file"
    }

    fun dataToFeed (file: File, context:File) {
        val fileContent = file.readText()
        Log.i(TAG, "Received File Content = "+fileContent)
        val feedList = fileContent.split("#####")

        var feedNamesFile = File(context, Constants.FEED_NAMES_FILE)
        var feedNameList = feedNamesFile.readText().split("\n")

        val fdp = FeedDataParser()

        val newEntryList = mutableListOf<Entry>()

        for (feed in feedList) {
            Log.i(TAG, "Feed = "+feed)
            val feedElem = feed.split("_:_:_")
            val feedName = feedElem[0]
            Log.i(TAG, "FeedName = "+feedName)
            val feedContent = feedElem[1]
            Log.i(TAG, "FeedContent = "+feedContent)
            for (person in feedNameList) {
                if (person.equals(feedName)) {
                    Log.i(TAG, "Feed is subscribed")
                    val personFile = File(context, person)
                    val entrylist = fdp.feedToEntrylist(personFile)
                    var len = entrylist.size
                    val lastEntry = entrylist[len-1]

                    val supportFile = File(context, "support")
                    supportFile.createNewFile()
                    supportFile.appendText(feedContent)
                    Log.i(TAG, "Support File Content = "+supportFile.readText())
                    val feedEntryList = fdp.feedToEntrylist(supportFile)
                    len = feedEntryList.size
                    val lastEntryInFeed = feedEntryList[len-1]

                    Log.i(TAG, "Old Feed Content = "+personFile.readText())

                    if (lastEntry.id < lastEntryInFeed.id) {
                        for (i in (lastEntry.id+1)..lastEntryInFeed.id) {
                            newEntryList.add(feedEntryList[i])
                        }
                        personFile.delete()
                        personFile.createNewFile()
                        personFile.appendText(supportFile.readText())
                        Log.i(TAG, "New Feed Content = "+personFile.readText())
                        break;
                    }
                }
            }
        }
        if (newEntryList.size > 0) {
            Log.i(TAG, "New Entries")
            val userNameFile = File(context, Constants.U_NAME_FILE)
            var userName = userNameFile.readLines().toString()
            userName = userName.substring(1, userName.length - 1)
            var personData = File(context, Constants.PERSON_DATA)
            val content = personData.readText()
            val keys = content.split(" ")
            val pubkey = keys[0].split("_")
            val salt = pubkey[1].takeLast(4)
            val feedname = userName.plus("#").plus(salt)

            val ownPublisher = OwnPublisher(userName, keys[0], keys[1])

            var ownFeedFile = File(context, feedname)
            var ownFeedList = fdp.feedToEntrylist(ownFeedFile)

            val ownFeed = OwnFeed(ownFeedList, ownPublisher)

            var logEntry = LogEntry.Companion.newLogEntry(newEntryList, ownFeed, context)

            val appendtext = fdp.appendCacheToFeed(logEntry)
            Log.i(TAG, "Append Text = "+appendtext)
            if (ownFeedFile.length() == 0L) {
                ownFeedFile.appendText(appendtext)
            } else {
                ownFeedFile.appendText("-*-*-")
                ownFeedFile.appendText("".plus(appendtext))
            }
            Log.i(TAG, "Own Feed Content = "+ownFeedFile.readText())
            val generator = CacheListGenerator()
            generator.getCacheListFileContent(context)
        }
    }

}