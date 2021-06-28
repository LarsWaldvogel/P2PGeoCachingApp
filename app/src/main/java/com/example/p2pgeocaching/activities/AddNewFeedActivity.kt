package com.example.p2pgeocaching.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.constants.Constants
import com.example.p2pgeocaching.databinding.ActivityAddNewFeedBinding
import java.io.File

/**
 * This activity enables the user to add a new Feed to their subscriptions.
 */
class AddNewFeedActivity : AppCompatActivity() {

    // TODO

    companion object {
        const val TAG = "AddNewFeedActivity"
    }

    private lateinit var binding: ActivityAddNewFeedBinding

    /**
     * Opens files, sets up button
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object
        binding = ActivityAddNewFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        val context = applicationContext
        val feedListFile = File(context.filesDir, Constants.U_NAME_FILE)

        binding.saveFeedButton.setOnClickListener {
            // TODO: verify input if it is legal
            //  use inputValidator for the username, and check if the public key
            //  is 4 digits (salt)
            //  if everything is legal, save it to the file
            //  then return
            finish()
        }
    }

}