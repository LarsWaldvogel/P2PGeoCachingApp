package com.example.p2pgeocaching.Caches

import com.example.p2pgeocaching.InputValidator.Companion.checkForIllegalCharacters
import com.example.p2pgeocaching.p2pexceptions.CreatorNotInHallOfFameException
import com.example.p2pgeocaching.p2pexceptions.KeysDoNotMatchException
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

// TODO: cache should update with bluetooth connection transfer
/**
 * This class saves all the data related to a cache.
 * Cache class can be called in two ways, either to create a new cache or to save an existing one.
 * The title of the cache is saved in [title], its description in [desc], neither may contain an
 * illegal character.
 * [creator] contains the name of the cache's creator, also no illegal characters allowed.
 * The unique identifier [id] is generated by concatenating [title], ';', [desc], then hashing them.
 * The public key is saved in [pubKey], the private key  in [prvKey].
 * The [hallOfFame] contains the encrypted name of the people that have completed the cache.
 * It saves them as an array of ByteArrays.
 * The only item contained at initialization is the [creator].
 * To decrypt, the [pubKey] is used.
 * For encrypting, you need the [prvKey], which you receive upon finding the physical cache.
 */
open class Cache(
    private val title: String,
    private val desc: String,
    private val creator: String,
    protected var id: Int,
    protected var pubKey: PublicKey?,
    protected var prvKey: PrivateKey?,
    protected var hallOfFame: MutableList<ByteArray>?
) {
    protected var plainTextHOF: String = ""

    /**
     * Constructor for saving existing cache.
     * This assumes, all information of the cache is known, except for the [prvKey].
     */
    constructor(
        title: String,
        desc: String,
        creator: String,
        id: Int,
        pubKey: PublicKey,
        hallOfFame: MutableList<ByteArray>?
    ) : this(title, desc, creator, id, pubKey, null, hallOfFame) {
        // This checks if the arguments contain an illegal character, which it should not
        val argList: ArrayList<String> = arrayListOf(title, desc, creator)
        checkForIllegalCharacters(argList)

        // This checks if the creator is in the [hallOfFame] list
        checkCreatorInHOF()
    }





    /**
     * This function takes an ByteArray containing a cipher text [byteCipher] as input
     * It returns the plain text as a String.
     * This decryption is done using the public key.
     */
    private fun decryptToString(byteCipher: ByteArray): String {
        // Setting up the object to decrypt
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, pubKey)

        // Here we actually decrypt the message and return it as a String
        val bytePlain = cipher.doFinal(byteCipher)
        return bytePlain.toString()
    }


    /**
     * This function decrypts the [hallOfFame] and returns it as a String.
     * Each entry of [hallOfFame] is separated by a ";" in the final String.
     * If [hallOfFame] is null, an empty string is returned.
     */
    fun hallToString(): String {
        var hofString = ""
        return if (hallOfFame == null) {
            ""
        } else {
            for (cipherEntry in hallOfFame!!) {
                hofString += decryptToString(cipherEntry) + ";"
            }
            hofString
        }
    }


    /**
     * This function takes the name of the [finder] and the [newPrvKey] that was found at the cache.
     * It checks if the [newPrvKey] is correct, then adds it to the cache.
     * With the [prvKey], it adds the [finder] to the [hallOfFame].
     * Throws keysDoNotMatchException and stringContainsIllegalCharacterException.
     */
    fun solveCache(finder: String, newPrvKey: PrivateKey) {
        // Check if keys match
        if (isValidKeypair(newPrvKey, pubKey)) {
            prvKey = newPrvKey
        } else {
            throw KeysDoNotMatchException()
        }

        // Finder cannot contain any illegal characters
        checkForIllegalCharacters(finder)

        // Adds the encrypted name to [hallOfFame], if it is null, creates a new one
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, prvKey)
        val encryptedFinder: ByteArray = cipher.doFinal(finder.toByteArray())
        if (hallOfFame == null) {
            hallOfFame = ArrayList()
        }
        hallOfFame!!.add(encryptedFinder)
    }


    /**
     * This function checks if the two keys [prv] and [pub] belong to one another.
     * If they do, returns true, else false.
     * It does this by checking if encrypting, then decrypting, does not change the sample string.
     */
    protected fun isValidKeypair(prv: PrivateKey, pub: PublicKey?): Boolean {
        val str = "some String"

        // Encrypt the String
        val encrypter: Cipher = Cipher.getInstance("RSA")
        encrypter.init(Cipher.ENCRYPT_MODE, prv)
        val cipherStr: ByteArray = encrypter.doFinal(str.toByteArray())

        // Decrypt the String
        val decrypter: Cipher = Cipher.getInstance("RSA")
        decrypter.init(Cipher.DECRYPT_MODE, pub)
        val plainStr: ByteArray = decrypter.doFinal(cipherStr)

        // Check if equal
        return str == plainStr.toString()
    }







    /**
     * Simple function that checks if the [creator] is in the [hallOfFame].
     * If [creator] is not contained, throws CreatorNotInHallOfFameException.
     */
    private fun checkCreatorInHOF() {
        val stringHOF: String = hallToString()
        if (!stringHOF.contains(creator)) {
            throw CreatorNotInHallOfFameException()
        }
    }


    /**
     * The toString() function now concatenates most things in human readable format.
     * Format is as follows: "<name>: <data>;", repeated.
     */
    override fun toString(): String {
        return "Title: $title; Description: $desc; Creator: $creator; ID: $id; " +
                "Public Key: $pubKey; Private key: $prvKey; Hall of Fame: $hallOfFame;"
    }


}