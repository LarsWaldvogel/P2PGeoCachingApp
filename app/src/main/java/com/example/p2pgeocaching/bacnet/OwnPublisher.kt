package com.example.p2pgeocaching.bacnet

/**
 * This is a subclass of [Publisher]. This one also has a [privateKey] to sign [Entry]s with.
 */
class OwnPublisher(name: String, publicKey: String, val privateKey: String) :
    Publisher(name, publicKey) {
}