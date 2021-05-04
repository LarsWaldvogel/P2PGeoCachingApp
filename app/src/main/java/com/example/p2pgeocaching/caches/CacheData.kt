package com.example.p2pgeocaching.caches

import java.security.PrivateKey
import java.security.PublicKey

/**
 * This cache saves the Data in a Cache.
 * All parameters are like the original [Cache] class, but the [type] represents which subclass it
 * fulfills: "OwnCache", "SolvedCache", "UnsolvedCache" or "TransferCache".
 * "TransferCache" is used, when we use the [CacheData] to transfer from one device to another,
 * it is equivalent to "UnsolvedCache".
 */
data class CacheData(
    val title: String,
    val desc: String,
    val creator: String,
    var id: Int,
    var pubKey: PublicKey?,
    var prvKey: PrivateKey?,
    var hallOfFame: MutableSet<ByteArray>?,
    var type: String
)