package com.example.p2pgeocaching.bluetooth


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket


import java.lang.Exception
import java.io.IOException
import java.util.*

class BluetoothServer(
    private val activity: BluetoothTransfer,
    private val socket: BluetoothSocket
) : Thread() {
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