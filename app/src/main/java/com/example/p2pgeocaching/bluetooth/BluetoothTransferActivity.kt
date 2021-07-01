package com.example.p2pgeocaching.bluetooth

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.activities.ConnectActivity
import com.example.p2pgeocaching.constants.Constants
import com.example.p2pgeocaching.databinding.BluetoothTransferBinding
import java.io.File

/**
 * This activity handles all bluetooth related things
 */
class BluetoothTransferActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BluetoothTransferActivity"
    }

    lateinit var listView: ListView
    lateinit var bluetoothHandler: BluetoothHandler
    var bluetoothActive = false

    private lateinit var binding: BluetoothTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(ConnectActivity.TAG, "Transfer")

        title = "Transfer"

        binding = BluetoothTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.device_list_view)

        // Opens the files used in the app for storage
        val context = applicationContext
        val userNameFile = File(context.filesDir, Constants.U_NAME_FILE)
        val cacheListFile = File(context.filesDir, Constants.CACHE_LIST_FILE)

        binding.discoverableSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = "ON"
                bluetoothHandler.discoverable(true)
            } else {
                buttonView.text = "OFF"
                bluetoothHandler.discoverable(false)
            }
        }

        // list with descovered devices hopefully including the one with the started server
        listView.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            Log.d(TAG, "You clicked on a device")
            val deviceName: String? = bluetoothHandler.devices[i]?.name
            val deviceAddress: String? = bluetoothHandler.devices[i]?.address
            Log.d(TAG, "You clicked on device: $deviceName, $deviceAddress")
            val device = bluetoothHandler.devices[i] // device?.address!!
            bluetoothHandler.connectToServer(device)
        }

        // start server and listen for connections
        binding.servbtn.setOnClickListener {
            bluetoothHandler.startServer()
        }

        // close sockets
        binding.closebtn.setOnClickListener {
            bluetoothHandler.stop()
        }
    }
}

