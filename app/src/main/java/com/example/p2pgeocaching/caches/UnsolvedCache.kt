package com.example.p2pgeocaching.caches

import com.example.p2pgeocaching.inputValidator.InputValidator
import com.example.p2pgeocaching.p2pexceptions.CreatorNotInHallOfFameException
import com.example.p2pgeocaching.p2pexceptions.KeysDoNotMatchException
import com.example.p2pgeocaching.p2pexceptions.ParametersAreNullException
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher


/**
 * [UnsolvedCache] is the subclass of [Cache] which is used when the user receives a new [Cache]
 * from somebody else.
 * It does not contain a [pubKey] yet.
 * For further documentation, see [Cache].
 */
class UnsolvedCache(
    title: String,
    desc: String,
    creator: String,
    id: Int,
    pubKey: PublicKey,
    hallOfFame: MutableSet<String>,
    plainTextHOF: String
) : Cache(title, desc, creator, id, pubKey, null, hallOfFame, plainTextHOF) {


    /**
     * Called when entering new caches that have been transferred from another device.
     * Here we check if the entries are all legal and if the creator is contained in the
     * [hallOfFame]. Then the [hallOfFame] is decrypted and saved in [plainTextHOF].
     */
    constructor(
        title: String,
        desc: String,
        creator: String,
        id: Int,
        pubKey: PublicKey,
        hallOfFame: MutableSet<String>
    ) : this(title, desc, creator, id, pubKey, hallOfFame, "") {

        // This checks if the arguments contain an illegal character, which it should not
        InputValidator.checkUserNameForIllegalCharacters(creator)
        InputValidator.checkTextForIllegalCharacters(listOf(title, desc))

        // This checks if the creator is in the [hallOfFame] list
        checkCreatorInHOF()

        // Here we decrypt and save the [hallOfFame] to [plainTextHOF]
        updatePlainTextHOF()
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
     * This function takes the name of the [finder] and the [newPrvKey] that was found at the cache.
     * It checks if the [newPrvKey] is correct, then adds it to the cache.
     * With the [prvKey], it adds the [finder] to the [hallOfFame].
     * Throws keysDoNotMatchException and stringContainsIllegalCharacterException.
     */
    fun solveCache(finder: String, newPrvKey: PrivateKey): SolvedCache {

        // Check if keys match
        if (isValidKeypair(newPrvKey, pubKey)) {
            prvKey = newPrvKey
        } else {
            throw KeysDoNotMatchException()
        }

        // Finder cannot contain any illegal characters
        InputValidator.checkUserNameForIllegalCharacters(finder)

        // Assert that all values are not null
        if (pubKey == null) {
            throw ParametersAreNullException()
        }

        // Create the [SolvedCache] object to return
        val solvedCache =
            SolvedCache(title, desc, creator, id, pubKey!!, newPrvKey, hallOfFame, plainTextHOF)

        // Adds the encrypted name to [hallOfFame], if it is null, creates a new one
        val cipher = Cipher.getInstance("com.example.p2pgeocaching.RSA.RSA")
        cipher.init(Cipher.ENCRYPT_MODE, prvKey)
        val encryptedFinder: String = cipher.doFinal(finder.toByteArray()).toString()

        // Here it is inserted into the new solvedCache object
        solvedCache.addPersonToHOF(encryptedFinder)

        return solvedCache
    }


    /**
     * This function checks if the two keys [prv] and [pub] belong to one another.
     * If they do, returns true, else false.
     * It does this by checking if encrypting, then decrypting, does not change the sample string.
     */
    private fun isValidKeypair(prv: PrivateKey, pub: PublicKey?): Boolean {
        val str = "some String"

        // Encrypt the String
        val encrypter: Cipher = Cipher.getInstance("com.example.p2pgeocaching.RSA.RSA")
        encrypter.init(Cipher.ENCRYPT_MODE, prv)
        val cipherByteArray: ByteArray = encrypter.doFinal(str.toByteArray())

        // Decrypt the String
        val decrypter: Cipher = Cipher.getInstance("com.example.p2pgeocaching.RSA.RSA")
        decrypter.init(Cipher.DECRYPT_MODE, pub)
        val plainStr: String = decrypter.doFinal(cipherByteArray).toString()

        // Check if equal
        return str == plainStr
    }


}