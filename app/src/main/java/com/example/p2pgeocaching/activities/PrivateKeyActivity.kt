package com.example.p2pgeocaching.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.databinding.ActivityPrivateKeyBinding

class PrivateKeyActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PrivateKeyActivity"
        const val PRIVATE_KEY = "private key"
    }

    private lateinit var binding: ActivityPrivateKeyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object
        binding = ActivityPrivateKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change title
        title = getString(R.string.private_key_title)

        // Get and show the private key, if there is none, return
        val privateKey = intent?.extras?.getSerializable(PRIVATE_KEY).toString()
        if (privateKey != "") {
            binding.privateKeyText.text = getString(R.string.private_key_headline, privateKey)
        } else {
            finish()
        }
    }
}