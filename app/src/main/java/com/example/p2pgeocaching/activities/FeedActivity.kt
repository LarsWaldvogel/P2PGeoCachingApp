package com.example.p2pgeocaching.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.constants.Constants.Companion.FEED_NAMES_FILE
import com.example.p2pgeocaching.databinding.ActivityFeedBinding
import java.io.File

/**
 * This activity enables the user to view their feeds.
 */
class FeedActivity : AppCompatActivity() {

    // TODO

    companion object {
        const val TAG = "FeedActivity"
    }

    private lateinit var binding: ActivityFeedBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedNameList: List<String>


    /**
     * Reads from files which feeds the user is subscribed to.
     * Displays them in recyclerList, when pressed on they open FeedDetailViewActivity
     * The button at the bottom is for adding new feeds, leads to AddNewFeedActivity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set title
        title = getString(R.string.feeds_title)


        // Opens the files used in the app for storage
        val context = applicationContext
        val feedsNameFile = File(context.filesDir, FEED_NAMES_FILE)

        // User has not subscribed to any feeds or is opening the app for the first time
        // show them a message
        if (!feedsNameFile.exists()) {
            feedNameList = mutableListOf()
            binding.emptyFeedListPromptText.text = getString(R.string.empty_feed_list_prompt)

        } else { // File exists
            // TODO: uncomment the following line once you have a real implementation
            //  and delete the one after
            // feedList = getFeedList()
            feedNameList = mutableListOf()

            // File exists, but is empty, show message
            if (feedNameList.isEmpty()) {
                binding.emptyFeedListPromptText.text = getString(R.string.empty_feed_list_prompt)

            } else { // List of feeds exists
                // TODO: show list of feeds with recyclerview
                TODO("Implement recyclerView")
            }
        }

        // When button is clicked, open activity to add new Feed
        binding.addFeedButton.setOnClickListener {
            val intent = Intent(context, AddNewFeedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}