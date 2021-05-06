package com.example.p2pgeocaching.data

import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.caches.CacheList
import java.io.File

/**
 * This class is used to transform data to [CacheList] and vice versa. Also writes to file.
 */
class CacheListDataParser {

    companion object {

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
         * This function takes the [CacheList] which it is given and saves it to the [cacheListFile].
         * This is usually called whenever the app closes.
         */
        fun saveCacheList(cacheList: CacheList, cacheListFile: File) {
            val listOfCacheData: MutableList<CacheData> = mutableListOf()
            for (cache in cacheList.list) {
                val dataCache = CacheDataParser.cacheToData(cache)
                    listOfCacheData.add(dataCache)
            }
            val cacheListData = CacheListData(listOfCacheData)

            // TODO write the cacheListData to the file (after erasing the file)
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
}