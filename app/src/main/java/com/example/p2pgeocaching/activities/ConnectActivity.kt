package com.example.p2pgeocaching.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.databinding.ActivityConnectBinding
import java.io.File

// TODO: list of people in area
class ConnectActivity : AppCompatActivity() {

    companion object {
        const val U_NAME_FILE = "userName"
        const val CACHE_LIST_FILE = "cacheList"
        const val TAG = "ConnectActivity"
    }

    private lateinit var binding: ActivityConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Connect"

        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        val context = applicationContext
        val userNameFile = File(context.filesDir, MainActivity.U_NAME_FILE)
        val cacheListFile = File(context.filesDir, MainActivity.CACHE_LIST_FILE)

        binding.transferButton.setOnClickListener {
            // TODO: implement bluetooth transfer
        }
    }

}