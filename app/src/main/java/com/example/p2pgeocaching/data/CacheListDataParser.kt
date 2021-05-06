package com.example.p2pgeocaching.data

import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.caches.CacheList
import java.io.File

class CacheListDataParser {


    /**
     * This function loads the [CacheList] from the given [cacheListFile] and
     * returns it as an object.
     * If the file does not exist, the list in the object is empty.
     */
    fun loadCacheList(cacheListFile: File): CacheList {

        // file is empty or does not exist: return a CacheList with empty list
        if (!cacheListFile.exists()) {
            return CacheList(mutableListOf())
        } else {
            val list = mutableListOf<Cache>()
            // TODO add the elements of the file to the list (JSON)
            return CacheList(list)

        }

    }


    /**
     * Returns a [CacheList] when given a [CacheListData].
     */
    fun dataToList(data: CacheListData): CacheList {
        val list = mutableListOf<Cache>()
        data.dataList.forEach {
            list.add(CacheDataParser.dataToCache(it))
        }
        return CacheList(list)
    }


    /**
     * Creates a [CacheListData] when given a [CacheList].
     */
    fun listToData(cacheList: CacheList): CacheListData {
        val newList = mutableListOf<CacheData>()
        cacheList.list.forEach {
            newList.add(CacheDataParser.cacheToData(it))
        }
        return CacheListData(newList)
    }
}