package com.example.p2pgeocaching.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.p2pgeocaching.R

/**
 * This class notices all bluetooth related changes such as the current state of the adapter(on,off, etc.)
 */
class BluetoothBroadcast(
    private val bluetoothHandler: BluetoothHandler,
    val activity: BluetoothTransferActivity): BroadcastReceiver() {

    companion object {
        const val TAG = "BluetoothBroadcast"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val state: Int = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d(TAG, "STATE OFF")
                    }

                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.d(TAG, "STATE TURNING OFF")
                    }

                    BluetoothAdapter.STATE_ON -> {
                        Log.d(TAG, "STATE ON")
                    }

                    BluetoothAdapter.STATE_TURNING_ON -> {
                        Log.d(TAG, "STATE TURNING ON")
                    }
                }
            }

            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                val scanMode: Int = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)

                when(scanMode) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                        Log.d(TAG, "Discoverability Enabled")
                    }

                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                        Log.d(TAG, "Discoverability Disabled. Able to receive connections")
                    }

                    BluetoothAdapter.SCAN_MODE_NONE -> {
                        Log.d(TAG, "Discoverability Disabled. Not able to receive connections")
                    }

                    BluetoothAdapter.STATE_CONNECTING -> {
                        Log.d(TAG, "Connecting ...")
                    }

                    BluetoothAdapter.STATE_CONNECTED -> {
                        Log.d(TAG, "Connected.")
                    }
                }
            }

            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                println( "Device found: ${device?.name}:${device?.address}")
                if (!bluetoothHandler.devices.contains(device)) {
                    bluetoothHandler.devices.add(device)
                    bluetoothHandler.bluetoothDeviceListAdapter = BluetoothDeviceListAdapter(
                        context,
                        R.layout.device_adapter_view,
                        bluetoothHandler.devices
                    )
                    activity.listView.adapter = bluetoothHandler.bluetoothDeviceListAdapter
                }
            }

            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                when(device?.bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        Log.d(TAG, "BOND_BOONDED")
                        //activity.device = device
                    }

                    BluetoothDevice.BOND_BONDING -> {
                        Log.d(TAG, "BOND_BONDING")
                    }

                    BluetoothDevice.BOND_NONE -> {
                        Log.d(TAG, "BOND_NONE")
                    }
                }
            }


        }
    }



}