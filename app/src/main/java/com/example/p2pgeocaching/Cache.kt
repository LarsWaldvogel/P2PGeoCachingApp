package com.example.p2pgeocaching

import android.security.keystore.KeyProperties
import com.example.p2pgeocaching.p2pexceptions.CreatorNotInHallOfFameException
import com.example.p2pgeocaching.p2pexceptions.KeysDoNotMatchException
import com.example.p2pgeocaching.p2pexceptions.StringContainsIllegalCharacterException
import java.security.*
import java.util.Objects.hash
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
class Cache(
    private val title: String,
    private val desc: String,
    private val creator: String,
    private var id: Int,
    private var pubKey: PublicKey?,
    private var prvKey: PrivateKey?,
    private var hallOfFame: MutableList<ByteArray>?
    private var plainTextHOF: String // TODO implement this
) {


    /**
     * Constructor for creating a new cache.
     * Here, we only give it the [title], the [desc] and the [creator]. The rest is derived from it.
     */
    constructor(title: String, desc: String, creator: String) : this(
        title,
        desc,
        creator,
        -1,
        null,
        null,
        null
    ) {
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

        // Here we encrypt [creator] and add it to [hallOfFame]
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, prvKey)
        val encryptedCreator: ByteArray = cipher.doFinal(creator.toByteArray())
        if (hallOfFame == null) {
            hallOfFame = ArrayList()
        }
        hallOfFame!!.add(encryptedCreator)
    }


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
    fun foundCache(finder: String, newPrvKey: PrivateKey) {
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
    private fun isValidKeypair(prv: PrivateKey, pub: PublicKey?): Boolean {
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
     * Simple function that checks if any of the Strings provided in [arguments] contains an illegal
     * character.
     * If it does, throws a StringContainsIllegalCharacterException().
     */
    private fun checkForIllegalCharacters(arguments: List<String>) {
        // This contains a list of all the illegal characters
        val illegalCharacters: List<Char> = arrayListOf(';', '{', '}', '"')

        // Checks for illegal characters in strings
        for (str in arguments) {
            for (illChr in illegalCharacters) {
                if (str.contains(illChr)) {
                    throw StringContainsIllegalCharacterException()
                }
            }
        }
    }

    /**
     * If only a single string is provided, casts it to array list and calls original function.
     */
    private fun checkForIllegalCharacters(argument: String) {
        checkForIllegalCharacters(arrayListOf(argument))
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