package com.example.p2pgeocaching.bluetooth


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket


import java.lang.Exception
import java.io.IOException
import java.util.*

class BluetoothServerController(activity: BluetoothTransfer,bluetoothAdapter: BluetoothAdapter) : Thread() {

    val uuid: UUID = UUID.fromString("P2P-Bluetooth-Connection")
    private var cancelled: Boolean
    private val serverSocket: BluetoothServerSocket?
    private val activity = activity

    init {
        val btAdapter = bluetoothAdapter // TODO get adapter from other class (SetUp)
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
                BluetoothServer(activity, socket).start()
            }
        }
    }

    fun cancel() {
        this.cancelled = true
        this.serverSocket!!.close()
    }
}

class BluetoothServer(private val activity: BluetoothTransfer, private val socket: BluetoothSocket): Thread() {
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