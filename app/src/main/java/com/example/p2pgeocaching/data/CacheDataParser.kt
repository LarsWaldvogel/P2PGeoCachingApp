package com.example.p2pgeocaching.data

import com.example.p2pgeocaching.caches.*
import com.example.p2pgeocaching.p2pexceptions.CacheDataTypeNotDefinedException
import com.example.p2pgeocaching.p2pexceptions.IllegalCacheTypeException
import com.example.p2pgeocaching.p2pexceptions.ParametersAreNullException

/**
 * This class is used to parse from data objects to real objects and back.
 */
class CacheDataParser {

    companion object {


        /**
         * This is used to create a [Cache] from a [CacheData] object.
         * [data] is the object to be read from.
         */
        fun dataToCache(data: CacheData): Cache {
            return when (data.type) {
                OWN_CACHE -> dataToOwnCache(data)
                SOLVED_CACHE -> dataToSolvedCache(data)
                UNSOLVED_CACHE -> dataToUnsolvedCache(data)
                TRANSFER_CACHE -> dataToUnsolvedCache(data) // used when transferring caches
                else -> throw CacheDataTypeNotDefinedException()
            }
        }


        /**
         * Simple function that makes a [OwnCache] from a [CacheData] object [data].
         */
        private fun dataToOwnCache(data: CacheData): Cache {
            return OwnCache(data.title, data.desc, data.creator)
        }


        /**
         * Simple function that makes a [SolvedCache] from a [CacheData] object [data].
         */
        private fun dataToSolvedCache(data: CacheData): Cache {
            if (data.pubKey != null && data.prvKey != null) {
                return SolvedCache(
                    data.title,
                    data.desc,
                    data.creator,
                    data.id,
                    data.pubKey!!,
                    data.prvKey!!,
                    data.hallOfFame
                )
            } else {
                throw ParametersAreNullException()
            }
        }


        /**
         * Simple function that makes a [UnsolvedCache] from a [CacheData] object [data].
         */
        private fun dataToUnsolvedCache(data: CacheData): Cache {
            if (data.pubKey != null) {
                return UnsolvedCache(
                    data.title,
                    data.desc,
                    data.creator,
                    data.id,
                    data.pubKey!!,
                    data.hallOfFame
                )
            } else {
                throw ParametersAreNullException()
            }
        }


        /**
         * This function takes a [Cache] [cache] and transforms it into a [CacheData] object.
         */
        fun cacheToData(cache: Cache): CacheData {
            return when (cache) {
                is OwnCache -> CacheData(
                    cache.title,
                    cache.desc,
                    cache.creator,
                    cache.id,
                    cache.pubKey,
                    cache.prvKey,
                    cache.hallOfFame,
                    OWN_CACHE
                )
                is UnsolvedCache -> CacheData(
                    cache.title,
                    cache.desc,
                    cache.creator,
                    cache.id,
                    cache.pubKey,
                    null,
                    cache.hallOfFame,
                    UNSOLVED_CACHE
                )
                is SolvedCache -> CacheData(
                    cache.title,
                    cache.desc,
                    cache.creator,
                    cache.id,
                    cache.pubKey,
                    cache.prvKey,
                    cache.hallOfFame,
                    SOLVED_CACHE
                )
                else -> throw IllegalCacheTypeException()
            }
        }


        /**
         * This function is used when creating [CacheData] objects to transfer caches from one device
         * to another. It is effectively equivalent to the object created when using cacheToData
         * with an [UnsolvedCache].
         */
        fun cacheToTransfer(cache: Cache): CacheData {
            return CacheData(
                cache.title,
                cache.desc,
                cache.creator,
                cache.id,
                cache.pubKey,
                null,
                cache.hallOfFame,
                TRANSFER_CACHE
            )
        }


        /**
         * Returns a [CacheList] when given a [CacheListData].
         */
        fun dataToList(data: CacheListData): CacheList {
            val list = mutableListOf<Cache>()
            data.dataList.forEach {
                list.add(dataToCache(it))
            }
            return CacheList(list)
        }


        /**
         * Creates a [CacheListData] when given a [CacheList].
         */
        fun listToData(cacheList: CacheList): CacheListData {
            val newList = mutableListOf<CacheData>()
            cacheList.list.forEach {
                newList.add(cacheToData(it))
            }
            return CacheListData(newList)
        }
    }
}