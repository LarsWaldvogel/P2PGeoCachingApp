package com.example.p2pgeocaching.bacnet

/**
 * Publisher is the owner of a [Feed]. They are uniquely identified through their [publicKey].
 * They will sign the [Entry]s in their feed with their own private key, verifying their identity.
 */
open class Publisher(val name: String, val publicKey: String) {
    // TODO: implement
}