package com.example.p2pgeocaching.bluetooth


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.*
import android.os.Bundle


import java.lang.Exception
import java.io.IOException
import java.util.*

import com.example.p2pgeocaching.MainActivity // TODO change MainActivity class to BluetoothTransfer

class BluetoothServerController(activity: MainActivity) : Thread() {

    val uuid: UUID = UUID.fromString("P2P-Bluetooth-Connection")
    private var cancelled: Boolean
    private val serverSocket: BluetoothServerSocket?
    private val activity = activity
    private val device: DetectDevice = DetectDevice()

    init {
        val btAdapter = device.bluetoothAdapter // TODO get adapter from other class (SetUp)
        if (btAdapter != null) {
            this.serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("P2P", uuid)
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
                BluetoothServer(this.activity, socket).start()
            }
        }
    }

    fun cancel() {
        this.cancelled = true
        this.serverSocket!!.close()
    }
}

class BluetoothServer(private val activity: MainActivity, private val socket: BluetoothSocket): Thread() {
    private val inputStream = this.socket.inputStream
    private val outputStream = this.socket.outputStream

    override fun run() {
        try {
            // TODO look for terminator symbol
            // read bytes/cache from client
            val available = inputStream.available()
            val bytes = ByteArray(available)
            inputStream.read(bytes, 0, available)
            val text = String(bytes)
            // send text to cache
            // TODO compare caches and then send the different caches back
        } catch (e: Exception) {
            println("Input exception server")
        }
    }

    fun close() {
        outputStream.close()
        inputStream.close()
        this.socket.close()
    }
}