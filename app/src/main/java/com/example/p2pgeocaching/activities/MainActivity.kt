package com.example.p2pgeocaching.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.adapter.CacheAdapter
import com.example.p2pgeocaching.caches.CacheList
import com.example.p2pgeocaching.databinding.ActivityMainBinding
import java.io.File


// TODO add manifest to get bluetooth permissions
// TODO add user interface
// TODO add bluetooth transfer function
// TODO add the RecyclerList
/**
 * This activity serves as the center of the app.
 * From here, we can change our name, create a new cache, look at our caches and transfer caches
 * with others.
 */
class MainActivity : AppCompatActivity() {


    companion object {
        const val U_NAME_FILE = "userName"
        const val CACHE_LIST_FILE = "cacheList"
        const val TAG = "MainActivity"
    }

    // TODO transfer cacheList between activities
    lateinit var cacheList: CacheList
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView


    /**
     * This method reads the files.
     * Also, prompts the user for a name if there is none saved.
     * Offers some buttons for different options.
     *
     */
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

        } else { // Username has been selected, show it in title
            var userName = userNameFile.readLines().toString()
            userName = userName.substring(1, userName.length - 1)
            Log.d(TAG, userName)
            title = getString(R.string.welcome_message, userName)
        }

        // Show initialize the CacheList field
        if (cacheListFile.exists()) {
            // TODO initialize list
        } else { // File is empty
            cacheList = CacheList(mutableListOf())
        }

        // Update recyclerView to show list (if it is not empty)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CacheAdapter(cacheList)

        // TODO if cacheList is empty, hide recyclerView
        //  else hide message
        //  also do this in onRestart()



        // Opens rename activity when pressed
        binding.changeUserNameButton.setOnClickListener {
            val intent = Intent(context, UserNameActivity::class.java)
            intent.putExtra(U_NAME_FILE, userNameFile)
            Log.d(TAG, "Made it past putExtra")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)

        }

        // Opens activity to connect to others
        binding.connectButton.setOnClickListener {
            val intent = Intent(context, ConnectActivity::class.java)
            intent.putExtra(U_NAME_FILE, userNameFile)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)
        }

        // Opens activity to create caches
        binding.createCacheButton.setOnClickListener {
            val intent = Intent(context, NewCacheActivity::class.java)
            intent.putExtra(U_NAME_FILE, userNameFile)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Better alternative?
            context.startActivity(intent)
        }

    }


    /**
     * When coming back from another activity, update the title.
     */
    override fun onRestart() {
        super.onRestart()

        // Gets context and file
        val context = applicationContext
        val userNameFile = File(context.filesDir, U_NAME_FILE)

        // Updates title
        if (userNameFile.exists()) {
            var userName = userNameFile.readLines().toString()

            // Remove the first and last characters (which are not needed)
            userName = userName.substring(1, userName.length - 1)
            Log.d(TAG, userName)
            title = getString(R.string.welcome_message, userName)
        }
    }
}


// TODO: user interface needs the following screens:
//  - list of all caches with button to "create cache" and "connect to others" (main menu)
//  - forms to create cache (with input validation)
//  - page that shows the private key of owned cache
//  - cache detail view with hallOfFame and "found it" button
//  - form to enter found private key
//  - screen for bluetooth transfer
