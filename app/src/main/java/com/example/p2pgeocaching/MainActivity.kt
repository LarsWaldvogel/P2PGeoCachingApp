package com.example.p2pgeocaching

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.databinding.ActivityMainBinding
import java.io.File


// TODO add manifest to get bluetooth permissions
// TODO add user interface
// TODO add bluetooth transfer function
class MainActivity : AppCompatActivity() {

    companion object {
        const val U_NAME_FILE = "userName"
        const val CACHE_LIST_FILE = "cacheList"
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Opens the files used in the app for storage
        val context = applicationContext
        val userNameFile = File(context.filesDir, U_NAME_FILE)
        val cacheListFile = File(context.filesDir, CACHE_LIST_FILE)


        // Username not selected -> open screen to ask for name
        if (!userNameFile.exists()) {
            Log.d(TAG, "Made it past userNameFile.exists check")
            val intent = Intent(context, UserNameActivity::class.java)
            intent.putExtra(U_NAME_FILE, userNameFile)
            Log.d(TAG, "Made it past putExtra")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)

        } // Username is now selected

        // cacheList is empty -> show empty screen with option to create a Cache
        if (!cacheListFile.exists()) {

        } // caches now exist


    }
}





// TODO: user interface needs the following screens:
//  - when first opened: "what is your name?" screen which saves username
//  - list of all caches with button to "create cache" and "connect to others" (main menu)
//  (when empty show "connect or create"-message)
//  - forms to create cache (with input validation)
//  - page that shows the private key of owned cache
//  - cache detail view with hallOfFame and "found it" button
//  - form to enter found private key
//  - screen for bluetooth transfer
