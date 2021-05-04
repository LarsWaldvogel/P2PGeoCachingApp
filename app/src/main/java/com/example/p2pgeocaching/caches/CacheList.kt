package com.example.p2pgeocaching.caches

// TODO: list should update with bluetooth transfer
/** Contains the list of caches of the device
 * Is updated upon file transfer via bluetooth
 */
class CacheList(var list: MutableList<Cache>) {


    /**
     * This function checks if the provided caches are already in the list.
     * If yes, it checks if the hallOfFame has any differences.
     * If not, adds it to the list.
     */
    fun add(caches: List<Cache>) {
        for (newCache in caches) {

            // Set to true if the object of the new caches is already in the list (meaning it does
            // not have to be added again)
            var isInList = false

            // Now we look at all caches in the list
            list.forEach {

                // The cache is already on the device. We will potentially add people to the
                // hallOfFame
                if (it.id == newCache.id) {
                    isInList = true
                    newCache.addPeopleToHOF(it.hallOfFame)
                }
            }

            // if the cache is not already in the list, add it
            if (!isInList) {
                list.add(newCache)
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