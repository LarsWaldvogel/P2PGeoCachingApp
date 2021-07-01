package com.example.p2pgeocaching.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.bluetooth.BluetoothTransferActivity
import com.example.p2pgeocaching.constants.Constants.Companion.CACHE_LIST_FILE
import com.example.p2pgeocaching.constants.Constants.Companion.U_NAME_FILE
import com.example.p2pgeocaching.databinding.ActivityConnectBinding
import java.io.File

// TODO: list of people in area

/**
 * This is used to set up connections to surrounding people.
 */
class ConnectActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ConnectActivity"
    }

    private lateinit var binding: ActivityConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "ConnectActivity has been opened.")

        title = "Connect"

        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Opens the files used in the app for storage
        val context = applicationContext
        val userNameFile = File(context.filesDir, U_NAME_FILE)
        val cacheListFile = File(context.filesDir, CACHE_LIST_FILE)

        binding.transferButton.setOnClickListener {
            Log.d(TAG, "TransferButton has been pressed.")
            val intent = Intent(this, BluetoothTransferActivity::class.java)
            startActivity(intent)
            // TODO: implement bluetooth transfer
        }
    }

}