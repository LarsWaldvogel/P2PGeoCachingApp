package com.example.p2pgeocaching.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.p2pgeocaching.R

/**
 * Sends list of all caches to other device and receives the other device's list of caches
 * Uses byte stream to transfer JSON files of the CacheList objects
 */

class BluetoothTransfer : Activity() {

    companion object {
        const val TAG = "BluetoothTransfer"
    }

    private lateinit var bluetoothAdapter: BluetoothAdapter
    var listOfDevices: Set<BluetoothDevice>? = null
    var arrayOfDevices: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        arrayOfDevices = ArrayAdapter(this, R.layout.bluetooth_select_device)
        if (bluetoothAdapter != null) {
            discoverDevices()
        }
        BluetoothServerController(this, bluetoothAdapter).start()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
/*    private val receiver = object : BroadcastReceiver() {
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
                        val deviceInfo = "$deviceName $deviceHardwareAddress"
                        println(deviceInfo)
                    }

                }
            }
        }
    }
*/

    /**
     * This method discovers devices near you and lists these  in [listOfDevices]
     */
    private fun discoverDevices() {
        // already paired devices
        listOfDevices = bluetoothAdapter.bondedDevices
        for (device in (listOfDevices as MutableSet<BluetoothDevice>?)!!) {
            // Add the name and address to an array adapter to show in a ListView
            arrayOfDevices!!.add((if (device.name != null) device.name else "Unknown") + "\n" + device.address + "\nPaired")
        }
        val client = ChooseClient()

        Log.d(TAG, "Client is: $client")
        Log.d(TAG, "FragmentManager is: ${client.fragmentManager.toString()}")

        // FIXME: fragmentManager is null
        //  possible error sources: wrong name scheme, no .xml exists
        client.show(client.fragmentManager!!, "client_chosen")

/* uncomment if you want to discover devices from inside the app
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery() // searches for devices (12 sec)

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter)
*/
    }

    override fun onDestroy() {
        super.onDestroy()
        // bluetoothAdapter.cancelDiscovery()
        // unregisterReceiver(receiver)
    }
}