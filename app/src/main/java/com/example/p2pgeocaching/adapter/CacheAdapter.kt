package com.example.p2pgeocaching.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.activities.OwnCacheDetailActivity
import com.example.p2pgeocaching.activities.SolvedCacheDetailActivity
import com.example.p2pgeocaching.activities.UnsolvedCacheDetailActivity
import com.example.p2pgeocaching.caches.CacheList
import com.example.p2pgeocaching.data.CacheData
import com.example.p2pgeocaching.data.CacheDataParser
import com.example.p2pgeocaching.data.CacheDataParser.Companion.OWN_CACHE
import com.example.p2pgeocaching.data.CacheDataParser.Companion.UNSOLVED_CACHE

/**
 * This class serves as the link between the recyclerView and the cacheList
 */
class CacheAdapter(val cacheList: CacheList) :
    RecyclerView.Adapter<CacheAdapter.CacheViewHolder>() {


    /**
     * Contains reference on how to display the items in the list
     */
    class CacheViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.cache_list_item)
    }


    /**
     * Returns the number of items in the list
     */
    override fun getItemCount(): Int {
        return cacheList.list.size
    }


    /**
     * Creates a new view with R.layout.item_view as its layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.cache_list_item, parent, false)

        // Setup custom accessibility delegate to set the text read
        layout.accessibilityDelegate = Accessibility
        return CacheViewHolder(layout)
    }


    /**
     * Replaces the content of an existing view with new data
     */
    override fun onBindViewHolder(holder: CacheViewHolder, position: Int) {

        // Saves the cache to item as data in the button
        val item: CacheData = CacheDataParser.cacheToData(cacheList.list[position])
        holder.button.text = item.title

        // What to do when clicked
        holder.button.setOnClickListener {
            val context = holder.view.context

            // Set intent to the type of cache to open, and add cache to extras
            val intent: Intent
            when (item.type) {
                OWN_CACHE -> {
                    intent = Intent(context, OwnCacheDetailActivity::class.java)
                    intent.putExtra(OwnCacheDetailActivity.CACHE, item)
                }
                UNSOLVED_CACHE -> {
                    intent = Intent(context, UnsolvedCacheDetailActivity::class.java)
                    intent.putExtra(UnsolvedCacheDetailActivity.CACHE, item)
                }
                else -> { // Solved cache
                    intent = Intent(context, SolvedCacheDetailActivity::class.java)
                    intent.putExtra(SolvedCacheDetailActivity.CACHE, item)
                }
            }

            // Start DetailActivity
            context.startActivity(intent)
        }
    }

    // Setup custom accessibility delegate to set the text read with
    // an accessibility service
    companion object Accessibility : View.AccessibilityDelegate() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onInitializeAccessibilityNodeInfo(
            host: View?,
            info: AccessibilityNodeInfo?
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            // With `null` as the second argument to [AccessibilityAction], the
            // accessibility service announces "double tap to activate".
            // If a custom string is provided,
            // it announces "double tap to <custom string>".
            val customString = host?.context?.getString(R.string.look_up_cache_detail)
            val customClick =
                AccessibilityNodeInfo.AccessibilityAction(
                    AccessibilityNodeInfo.ACTION_CLICK,
                    customString
                )
            info?.addAction(customClick)
        }
    }
}