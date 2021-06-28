package com.example.p2pgeocaching.ownbacnet

import com.example.p2pgeocaching.RSA.RSA

/**
 * This is a subclass of [Publisher]. This one also has a [privateKey] to sign [Entry]s with.
 */
class OwnPublisher(name: String, publicKey: String, val privateKey: String) :
    Publisher(name, publicKey) {

    // TODO

    /**
     * Signs the [plainText] by hashing it and encrypting it with the [privateKey].
     */
    fun sign(plainText: String): String {
        return RSA.encode(plainText.hashCode().toString(), privateKey)
    }
}