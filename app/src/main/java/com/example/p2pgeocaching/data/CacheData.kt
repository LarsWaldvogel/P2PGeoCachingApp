package com.example.p2pgeocaching.data

import java.io.Serializable
import java.security.PrivateKey
import java.security.PublicKey

/**
 * This cache saves the Data in a Cache.
 * All parameters are like the original Cache class, but the [type] represents which subclass it
 * fulfills: "OwnCache", "SolvedCache", "UnsolvedCache" or "TransferCache".
 * "TransferCache" is used, when we use the [CacheData] to transfer from one device to another,
 * it is equivalent to "UnsolvedCache", except the [plainTextHOF] will be reevaluated.
 */
data class CacheData(
    val title: String,
    val desc: String,
    val creator: String,
    var id: Int,
    var pubKey: PublicKey?,
    var prvKey: PrivateKey?,
    var hallOfFame: MutableSet<String>,
    var plainTextHOF: String,
    var type: String
) : Serializable {
    /**
     * This constructor is used when creating transfer files.
     */
    constructor(
        title: String,
        desc: String,
        creator: String,
        id: Int,
        pubKey: PublicKey?,
        prvKey: PrivateKey?,
        hallOfFame: MutableSet<String>,
        type: String
    ) : this(title, desc, creator, id, pubKey, prvKey, hallOfFame, "", type)
}