package com.example.p2pgeocaching.data

import android.util.Log
import com.example.p2pgeocaching.caches.CacheList
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class Serializer {

    companion object {

        private const val TAG = "Serializer"


        /**
         * Given the file [cacheListFile] containing the serialized version of the cache list, returns
         * the object encoded in it.
         */
        fun deserializeCacheList(cacheListFile: File): CacheList {

            // Read file, deserialize it, assign it to cacheList
            val cacheListDataString = cacheListFile.readBytes().toString()
            Log.d(TAG, "Read the following from file:\n$cacheListDataString")
            val cacheListData = Json.decodeFromString<CacheListData>(cacheListDataString)
            return CacheListDataParser.dataToList(cacheListData)
        }


        /**
         * This function serializes the [cacheList] and writes it to [cacheListFile].
         */
        fun serializeCacheList(cacheList: CacheList, cacheListFile: File) {

            val cacheListData = CacheListDataParser.listToData(cacheList)
            val serializedCacheList = Json.encodeToString(cacheListData)
            cacheListFile.delete()
            cacheListFile.writeText(serializedCacheList)
            Log.d(TAG, "Wrote the following to file:\n$serializedCacheList")
        }
    }
}