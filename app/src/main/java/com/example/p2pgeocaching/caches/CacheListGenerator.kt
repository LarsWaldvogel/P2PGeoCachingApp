package com.example.p2pgeocaching.caches

import com.example.p2pgeocaching.constants.Constants
import com.example.p2pgeocaching.data.FeedDataParser
import com.example.p2pgeocaching.data.Serializer
import com.example.p2pgeocaching.ownbacnet.Entry
import java.io.File
import android.util.Log

/**
 * This class looks at the local Feeds and updates the CacheList accordingly.
 * It is called whenever there are changes to any Feed that have new CacheEntries or HoFEntries.
 * Looks at all Feeds and all their Entries, if there is a Cache that another person has a
 * HoFEntry for, he is entered into the HallOfFame of the respective Cache.
 * If the user has a HoFEntry of their own for a cache, the cache is considered solved.
 * Caches in OwnFeed are OwnCaches.
 */
class CacheListGenerator {

    companion object {
        const val TAG = "CacheListGenerator"
    }

    private lateinit var cacheListFile: File

    fun getCacheListFileContent (context: File) {
        val userNameFile = File(context, Constants.U_NAME_FILE)
        val creatorString = userNameFile.readLines().toString()
        val creator = creatorString.substring(1, creatorString.length - 1)
        cacheListFile = File(context, Constants.CACHE_LIST_FILE)
        //val filename = "personData"
        var file = File(context, Constants.PERSON_DATA)
        val content = file.readText()
        val keys = content.split(" ")
        val pubkey = keys[0].split("_")
        val salt = pubkey[1].takeLast(4)
        val feedname = creator.plus("#").plus(salt)

        val feedFile = File(context, feedname)
        val fdp = FeedDataParser()
        val list = fdp.feedToEntrylist(feedFile)

        val feedNamesFile = File(context, Constants.FEED_NAMES_FILE)
        var cacheList = CacheList(mutableListOf())
        Log.i(TAG, "Length of feedNamesFile = "+feedNamesFile.length())
        if (feedNamesFile.length() == 0L) {
            Log.i(TAG, "No feeds")
            for (item in list) {
                if (item.content.equals(Constants.CACHE_ENTRY)) {
                    Log.i(TAG, "Item Content = "+item.content)
                    var cache1 = Serializer.deserializeCacheFromString(item.type)
                    var cache = OwnCache(
                        cache1.title,
                        cache1.desc,
                        cache1.creator,
                        cache1.id,
                        cache1.pubKey,
                        cache1.prvKey,
                        cache1.hallOfFame,
                        cache1.plainTextHOF
                    )
                    val ownCacheListFile = File(context, Constants.OWN_CACHE_LIST_FILE)
                    val ownCacheContent = ownCacheListFile.readText()
                    Log.i(TAG, "OwnCacheListFileContent = "+ownCacheContent)
                    val listOfTuples = ownCacheContent.split("\n")
                    for (tuple in listOfTuples) {
                        Log.i(TAG, "Tuple = "+tuple)
                        val tupleElem = tuple.split(":")
                        val sign = tupleElem[0]
                        val prvKey = tupleElem[1]
                        Log.i(TAG, "Signatur = "+sign)
                        Log.i(TAG, "PrvKey = "+prvKey)
                        if (sign.equals(item.signature)) {
                            cache.prvKey = prvKey.toString()
                        }
                    }
                    cacheList.add(cache)
                    Log.i(TAG, "Added cache "+cache.toString())
                }
            }
        } else {
            //TODO* not finished
            for (item in list) {
                if (item.content.equals(Constants.CACHE_ENTRY)) {
                    var cache1 = Serializer.deserializeCacheFromString(item.type)
                    var cache = OwnCache(
                        cache1.title,
                        cache1.desc,
                        cache1.creator,
                        cache1.id,
                        cache1.pubKey,
                        cache1.prvKey,
                        cache1.hallOfFame,
                        cache1.plainTextHOF
                    )
                    val feedNameList = feedNamesFile.readText().split("\n")
                    for (feedName in feedNameList) {
                        val feedFile = File(context, feedName)
                        val listOfEntries = fdp.feedToEntrylist(feedFile)
                        for (entry in listOfEntries) {
                            if (entry.content.equals(Constants.HOF_ENTRY) && entry.signature.equals(
                                    item.signature
                                )
                            ) {
                                //TODO* synchronize with HOFEntry
                                cache.addPersonToHOF(entry.type)
                            }
                        }
                    }
                    //TODO get privateKey from OwnCacheListFile
                    cacheList.add(cache)

                } else if (item.content.equals(Constants.LOG_ENTRY)) {
                    val feedNameList = feedNamesFile.readText().split("\n")
                    val listOfPeople = mutableListOf<String>()
                    var cache = Cache("", "", "", 0, "", "")
                    for (feedName in feedNameList) {
                        val feedFile = File(context, feedName)
                        val listOfEntries = fdp.feedToEntrylist(feedFile)
                        for (entry in listOfEntries) {
                            if (entry.content.equals(Constants.HOF_ENTRY) && entry.signature.equals(
                                    item.signature
                                )
                            ) {
                                //TODO* synchronize with HOFEntry
                                listOfPeople.add(entry.type)
                            }
                            if (entry.content.equals(Constants.CACHE_ENTRY) && entry.signature.equals(
                                    item.signature
                                )
                            ) {
                                cache = Serializer.deserializeCacheFromString(item.type)
                            }
                        }
                    }
                    for (person in listOfPeople) {
                        cache.addPersonToHOF(person)
                    }
                    for (myPerson in list) {
                        if (myPerson.type.equals(Constants.HOF_ENTRY) && myPerson.signature.equals(
                                item.signature
                            )
                        ) {
                            //TODO* synchronize with HOFEntry
                            cache.addPersonToHOF(myPerson.type)
                        }
                    }
                    cacheList.add(cache)
                }
            }
        }
        Serializer.serializeCacheListToFile(cacheList, cacheListFile)
    }
}