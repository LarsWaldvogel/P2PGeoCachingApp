package com.example.p2pgeocaching.bacnet

/**
 * This class represents the BaCNet-Feed.
 * [entries] contains all the entries of the feed, and [publisher] is the person that the feed
 * belongs to.
 */
open class Feed(val entries: List<Entry>, val publisher: Publisher) {
}