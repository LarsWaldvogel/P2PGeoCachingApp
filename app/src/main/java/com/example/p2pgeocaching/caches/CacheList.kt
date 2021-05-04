package com.example.p2pgeocaching.caches

import com.example.p2pgeocaching.p2pexceptions.HallOfFameEmptyException

// TODO: list should update with bluetooth transfer
/** Contains the list of caches of the device
 * Is updated upon file transfer via bluetooth
 */
class CacheList(var list: MutableList<Cache>?) {


    /**
     * This function checks if the provided caches are already in the list.
     * If yes, it checks if the hallOfFame has any differences.
     * If not, adds it to the list.
     */
    fun add(caches: List<Cache>) {

        // the list has not been initialized yet
        if (list == null) {
            list = caches.toMutableList()
        } else { // the list was initialized already

            // Iterate through caches list
            for (newCache in caches) {

                // Set to true if the object of the new caches is already in the list (meaning it does
                // not have to be added again)
                var isInList = false

                // Now we look at all caches in the list
                list!!.forEach {

                    // The cache is already on the device. We will potentially add people to the
                    // hallOfFame
                    if (it.id == newCache.id) {
                        isInList = true
                        if (it.hallOfFame != null) {
                            newCache.addPeopleToHOF(it.hallOfFame!!)
                        } else { // hallOfFame should never be null
                            throw HallOfFameEmptyException()
                        }
                    }
                }

                // if the cache is not already in the list, add it
                if (!isInList) {
                    list!!.add(newCache)
                }
            }
        }
    }


    /**
     * Simple function to use add with a single cache.
     */
    fun add(cache: Cache) {
        this.add(listOf(cache))
    }

}