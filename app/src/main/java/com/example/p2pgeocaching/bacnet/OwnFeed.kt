package com.example.p2pgeocaching.bacnet

/**
 * This is a [Feed] object, owned by the user, meaning it can be modified.
 */
class OwnFeed(entries: List<Entry>, ownPublisher: OwnPublisher) : Feed(entries, ownPublisher) {

    // TODO

    /**
     * Returns the [Publisher] as an [OwnPublisher] object (which it always is).
     */
    fun getOwnPublisher(): OwnPublisher {
        return publisher as OwnPublisher
    }
}