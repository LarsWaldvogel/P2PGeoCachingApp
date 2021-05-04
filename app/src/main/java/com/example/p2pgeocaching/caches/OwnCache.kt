package com.example.p2pgeocaching.caches

import android.security.keystore.KeyProperties
import com.example.p2pgeocaching.InputValidator.Companion.checkForIllegalCharacters
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.util.Objects.hash
import javax.crypto.Cipher

/**
 * [OwnCache] is the [Cache] subclass used when creating one's own [Cache].
 * For details on the inputs, see [Cache].
 * It is not shown in this file, but [OwnCache] inherits the [plainTextHOF] from its parent.
 * This is used to store a plain text of [hallOfFame], so it does not need to be decrypted
 * every time the user wants to look at it.
 */
class OwnCache(title: String, desc: String, creator: String) :
    Cache(title, desc, creator, -1, null, null, null) {


    /**
     * Here, with the inputs provided, we set the rest of the fields.
     */
    init {
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

        // TODO: make encrypt/decrypt their own functions
        // Here we encrypt [creator] and add it to [hallOfFame] and update [plainTextHOF]
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, prvKey)
        val encryptedCreator: ByteArray = cipher.doFinal(creator.toByteArray())

        // This is where the updates and inserts happen
        addPersonToHOF(encryptedCreator)
    }


    /**
     * This function generates a RSA key pair, which it returns.
     * The key is not generated with the [id] as initializer, because that could be replicated.
     */
    private fun generateKeyPair(): KeyPair {
        // This creates an object capable of
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)

        // The generator is initialized with a key length of 2048 bit and a random number
        generator.initialize(2048, SecureRandom())

        // Key pair is created and returned
        return generator.genKeyPair()
    }

}