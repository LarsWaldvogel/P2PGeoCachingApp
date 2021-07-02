package com.example.p2pgeocaching.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.p2pgeocaching.R
import com.example.p2pgeocaching.constants.Constants
import com.example.p2pgeocaching.databinding.ActivityBluetoothTransferBinding
import java.io.File


/**
 * This activity handles all bluetooth related things
 */
class BluetoothTransferActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BluetoothTransferActivity"
    }

    lateinit var listView: ListView
    lateinit var intentFilter: IntentFilter
    lateinit var bluetoothHandler: BluetoothHandler
    var bluetoothActive = false

    private lateinit var binding: ActivityBluetoothTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Transfer")

        title = "Transfer"

        binding = ActivityBluetoothTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.device_list_view)

        // Opens the files used in the app for storage
        val context = applicationContext
        val userNameFile = File(context.filesDir, Constants.U_NAME_FILE)
        val cacheListFile = File(context.filesDir, Constants.CACHE_LIST_FILE)

        while(!hasRequiredPermissions()){
            Log.i(TAG, "didn't have all required permissions!")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PackageManager.PERMISSION_GRANTED)
        }

        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothHandler = BluetoothHandler(this, bluetoothManager)

        val deviceList = bluetoothAdapter.bondedDevices
        var devices = ArrayList<BluetoothDevice?>()
        devices.addAll(deviceList)
        bluetoothHandler.devices = devices
        Log.i(TAG,"deviceList implemented size = ${devices.size}")

        listView.adapter = BluetoothDeviceListAdapter(
            context,
            R.layout.device_adapter_view,
            devices
        )

        intentFilter = IntentFilter()
        intentFilter.apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        }


        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, 3)
        }
        

        // list with descovered devices hopefully including the one with the started server
        listView.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            Log.i(TAG, "You clicked on a device")
            val deviceName: String? = bluetoothHandler.devices[i]?.name
            val deviceAddress: String? = bluetoothHandler.devices[i]?.address
            Log.i(TAG, "You clicked on device: $deviceName, $deviceAddress")
            val device = bluetoothHandler.devices[i] // device?.address!!
            bluetoothHandler.connectToServer(device, context.filesDir)
        }

        // start server and listen for connections
        binding.servbtn.setOnClickListener {
            bluetoothHandler.startServer(context.filesDir)
        }

        // scan for devices
        binding.scanbtn.setOnClickListener {
            bluetoothHandler.startDiscovery()
        }

        // close sockets
        binding.closebtn.setOnClickListener {
            bluetoothHandler.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "on Resume")
        registerReceiver(bluetoothHandler.state, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothHandler.state)
        bluetoothHandler.stop()
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasRequiredPermissions(): Boolean {
        val hasBluetoothPermission: Boolean = hasPermission(Manifest.permission.BLUETOOTH)
        Log.i(TAG, "hasBluetoothPermission = "+hasBluetoothPermission)
        val hasBluetoothAdminPermission: Boolean =
            hasPermission(Manifest.permission.BLUETOOTH_ADMIN)
        Log.i(TAG, "hasBluetoothAdminPermission = "+hasBluetoothAdminPermission)
        val hasLocationPermission: Boolean =
            hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        Log.i(TAG, "hasLocationPermission = "+hasLocationPermission)
        val hasFineLocationPermission: Boolean =
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        Log.i(TAG, "hasFineLocationPermission = "+hasFineLocationPermission)
        val hasBackgroundAccess: Boolean =
            hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        Log.i(TAG, "hasBackgroundAccess = "+hasBackgroundAccess)
        return hasBluetoothPermission && hasBluetoothAdminPermission && hasLocationPermission && hasFineLocationPermission
    }
}


