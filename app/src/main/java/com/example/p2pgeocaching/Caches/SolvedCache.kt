package com.example.p2pgeocaching.Caches;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *  This class represents a complete [Cache], which has been solved.
 *  It initializes the [PlainText
 */
class SolvedCache(
    title: String,
    desc: String,
    creator: String,
    id: Int,
    pubKey: PublicKey,
    prvKey: PrivateKey,
    hallOfFame: MutableSet<ByteArray>
) : Cache(title, desc, creator, id, pubKey, prvKey, hallOfFame) {


    /**
     * We only have to initialize [plainTextHOF]
     * (might not be necessary due to the superclass init, but just to be sure)
     */
    init {
        updatePlainTextHOF()
    }
}
