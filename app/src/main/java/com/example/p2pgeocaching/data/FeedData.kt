package com.example.p2pgeocaching.data

import android.provider.SyncStateContract
import com.example.p2pgeocaching.constants.Constants
import java.io.File
import java.io.Serializable

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
            file.appendText("\n".plus(ownFeedContent))
        }

        var feedNamesFile = File(context, Constants.FEED_NAMES_FILE)
        var feedNameList = feedNamesFile.readText().split("\n")
        for (feed in feedNameList) {
            val feedFile = File(context, feed)
            val feedContent = feedFile.readText()
            if (file.length() == 0L && feedContent.length != 0) {
                file.appendText(feed)
                file.appendText("\n".plus(feedContent))
            } else if (feedContent.length != 0) {
                file.appendText("\n#####")
                file.appendText("\n".plus(feed))
                file.appendText("\n".plus(feedContent))
            }
        }
        return "file"
    }

}