package com.example.p2pgeocaching.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.RSA.RSA
import com.example.p2pgeocaching.caches.Cache
import com.example.p2pgeocaching.caches.UnsolvedCache
import com.example.p2pgeocaching.data.Serializer
import com.example.p2pgeocaching.databinding.ActivitySolveBinding
import com.example.p2pgeocaching.inputValidator.InputValidator
import com.example.p2pgeocaching.p2pexceptions.KeyIsNotLegalException
import java.io.File

class SolveActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SolveActivity"
        const val U_NAME_FILE = "userName"
        const val CACHE_LIST_FILE = "cacheList"
        const val ID = "id"
    }

    private lateinit var binding: ActivitySolveBinding
    private lateinit var publicKey: String
    private var userName = ""
    private var cache: Cache? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivitySolveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        val context = applicationContext
        val userNameFile = File(context.filesDir, U_NAME_FILE)
        val cacheListFile = File(context.filesDir, CACHE_LIST_FILE)

        // Get username
        if (userNameFile.exists()) {
            userName = userNameFile.readLines().toString()

            // Remove the first and last characters (which are not needed)
            userName = userName.substring(1, userName.length - 1)
        }

        // Get cache
        val cacheList = Serializer.deserializeCacheListFromFile(cacheListFile)
        val cacheID = intent?.extras?.getInt(ID)
        if (cacheID == null) {
            finish()
            return
        }
        cache = cacheList.findByID(cacheID!!) as UnsolvedCache
        if (cache == null) {
            finish()
            return
        }

        // Change title
        title = getString(R.string.solve_title)

        // Get the public key
        publicKey =
            intent?.extras?.getSerializable(UnsolvedCacheDetailActivity.PUBLIC_KEY).toString()
        if (publicKey == "") {
            finish()
            return
        }

        binding.submitPrivateKeyButton.setOnClickListener {
            val privateKey = binding.privateKeyEditText.text.toString()

            // Check if it is valid input
            var isValidKey = false
            try {
                InputValidator.checkKey(privateKey)
                isValidKey = true
            } catch (e: KeyIsNotLegalException) {
            }
            if (!isValidKey) {
                binding.solveErrorText.text = getString(R.string.illegal_key_error)
            } else {

                // Check that keys match
                if (privateKey != "" && isValidKeypair(privateKey, publicKey)) {
                    if (cache is UnsolvedCache) {

                        // Solves cache, removes unsolved, adds solved, saves
                        val unsolvedCache = cache as UnsolvedCache
                        val solvedCache = unsolvedCache.solveCache(userName, privateKey)
                        cacheList.removeCacheByID(unsolvedCache.id)
                        cacheList.add(solvedCache)
                        Serializer.serializeCacheListToFile(cacheList, cacheListFile)
                    }
                    finish()
                } else {
                    binding.solveErrorText.text = getString(R.string.solve_cache_error)
                }
            }
        }
    }

    /**
     * This function checks if the two keys [prv] and [pub] belong to one another.
     * If they do, returns true, else false.
     * It does this by checking if encrypting, then decrypting, does not change the sample string.
     */
    private fun isValidKeypair(prv: String, pub: String): Boolean {
        val str = "some String"

        // Encrypt the String
        val cipherString = RSA.encode(str, prv)

        // Decrypt the String
        val plainString = RSA.decode(cipherString, pub)

        // Check if equal
        return str == plainString
    }
}