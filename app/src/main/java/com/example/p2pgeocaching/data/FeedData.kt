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

    fun feedToData(context: File): String {
        val file = File(context, "file")
        file.createNewFile()

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
            } else if (feedContent.length != 0) {
                file.appendText("#####")
                file.appendText("".plus(feed))
                file.appendText("_:_:_")
                file.appendText("".plus(feedContent))
            }
        }
        return "file"
    }

    fun dataToFeed (file: File, context:File) {
        val fileContent = file.readText()
        val feedList = fileContent.split("#####")

        var feedNamesFile = File(context, Constants.FEED_NAMES_FILE)
        var feedNameList = feedNamesFile.readText().split("\n")

        val fdp = FeedDataParser()

        val newEntryList = mutableListOf<Entry>()

        for (feed in feedList) {
            val feedElem = feed.split("_:_:_")
            val feedName = feedElem[0]
            val feedContent = feedElem[1]
            for (person in feedNameList) {
                if (person.equals(feedName)) {
                    val personFile = File(context, person)
                    val entrylist = fdp.feedToEntrylist(personFile)
                    var len = entrylist.size
                    val lastEntry = entrylist[len-1]

                    val supportFile = File(context, "support")
                    supportFile.createNewFile()
                    supportFile.appendText(feedContent)
                    val feedEntryList = fdp.feedToEntrylist(supportFile)
                    len = feedEntryList.size
                    val lastEntryInFeed = feedEntryList[len-1]

                    if (lastEntry.id < lastEntryInFeed.id) {
                        for (i in (lastEntry.id+1)..lastEntryInFeed.id) {
                            newEntryList.add(feedEntryList[i])
                        }
                        personFile.delete()
                        personFile.createNewFile()
                        personFile.appendText(supportFile.readText())
                        break;
                    }
                }
            }
        }
        if (newEntryList.size > 0) {
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
            Log.i(NewCacheActivity.TAG, "AppendText = "+appendtext)
            if (ownFeedFile.length() == 0L) {
                ownFeedFile.appendText(appendtext)
            } else {
                ownFeedFile.appendText("-*-*-")
                ownFeedFile.appendText("".plus(appendtext))
            }

            val generator = CacheListGenerator()
            generator.getCacheListFileContent(context)
        }
    }

}