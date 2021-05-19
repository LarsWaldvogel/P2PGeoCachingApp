package com.example.p2pgeocaching.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.*

class BluetoothServerController(activity: BluetoothTransfer,bluetoothAdapter: BluetoothAdapter) : Thread() {

    val uuid: UUID = UUID.fromString("P2P-Bluetooth-Connection")
    private var cancelled: Boolean
    private val serverSocket: BluetoothServerSocket?
    private val activity = activity

    init {
        if (bluetoothAdapter != null) {
            this.serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("P2P", uuid)
            this.cancelled = false
        } else {
            this.serverSocket = null
            this.cancelled = true
        }

    }

    override fun run() {
        var socket: BluetoothSocket

        while(true) {
            if (this.cancelled) {
                break
            }

            try {
                socket = serverSocket!!.accept()
            } catch(e: IOException) {
                break
            }

            if (!this.cancelled && socket != null) {
                // Start Server
                BluetoothServer(activity, socket).start()
            }
        }
    }

    fun cancel() {
        this.cancelled = true
        this.serverSocket!!.close()
    }
}