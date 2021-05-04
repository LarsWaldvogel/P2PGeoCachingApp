package com.example.p2pgeocaching.Caches

import java.security.KeyPair

class OwnCache(title: String, desc: String, creator: String) :
    Cache(title, desc, creator, -1, null, null, null) {

    private var plainTextHOF = null

    // This checks if the arguments contain an illegal character, which it should not
    val argList: ArrayList<String> = arrayListOf(title, desc, creator)
    checkForIllegalCharacters(argList)

    // Here we fabricate the string we want to hash by concatenating [title], ';' and [desc]
    val stringToHash = "$title;$desc"

    // The hash is saved to [id], which serves as the unique identifier of the cache
    id = hash(stringToHash)

    // The key pair is created and saved to [pubKey] and [prvKey]
    val keyPair: KeyPair = generateKeyPair()
    pubKey = keyPair.public
    prvKey = keyPair.private

}