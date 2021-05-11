package com.example.p2pgeocaching.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

/**
 * Sends list of all caches to other device and receives the other device's list of caches
 * Uses byte stream to transfer JSON files of the CacheList objects
 */

class BluetoothTransfer: Activity() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    var listOfDevices: Set<BluetoothDevice>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null) {
            println("bluetooth disabled")
        } else {
            discoverDevices()
        }
        BluetoothServerController(this, bluetoothAdapter).start()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                    val device = intent.getParcelableExtra<BluetoothDevice>( BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        listOfDevices = listOfDevices?.plus(device)
                        val deviceName = device.name
                        val deviceHardwareAddress = device.address // MAC address
                        val deviceInfo: String = "$deviceName $deviceHardwareAddress"
                    }

                }
            }
        }
    }

    /**
     * This method discovers devices near you and lists these  in [listOfDevices]
     */
    private fun discoverDevices() {
        // already paired devices
        listOfDevices = bluetoothAdapter.bondedDevices

        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery() // searches for devices (12 sec)

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter.cancelDiscovery()
        unregisterReceiver(receiver)
    }
}