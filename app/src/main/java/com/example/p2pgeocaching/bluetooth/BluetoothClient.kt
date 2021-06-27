package com.example.p2pgeocaching.bluetooth

import android.bluetooth.BluetoothDevice
import java.util.*

// call BluetoothClient(device).start() from main | device ist das zu verbindende Gerät
class BluetoothClient(device: BluetoothDevice, cache: String?) : Thread() {
    val uuid: UUID = UUID.fromString("P2P-Bluetooth-Connection")
    private val socket =
        device.createRfcommSocketToServiceRecord(uuid) // if this doesn't work, use: createInsecureRfcommSocketToServiceRecord(uuid)
    private var message = cache
    private var outputStream = this.socket.outputStream // Client sending
    private var inputStream = this.socket.inputStream // Client receive

    override fun run() {

        this.socket.connect() // Client connecting
        outputStream = this.socket.outputStream
        inputStream = this.socket.inputStream
        val length = message?.length
        var available = inputStream.available()

        try {
            if (length != null) {
                // TODO add a terminate symbol for better communication protocol
                outputStream.write(message?.toByteArray()) //send cache to server
                outputStream.flush()
            }
        } catch (e: Exception) {
            println("Output exception client")
        }
        try {
            // read bytes/cache from server
            val available = inputStream.available()
            val bytes = ByteArray(available)
            inputStream.read(bytes, 0, available)
            val newCache = String(bytes)
        } catch (e: java.lang.Exception) {
            println("Input exception client")
        }

    }

    fun close() {
        outputStream.close()
        inputStream.close()
        this.socket.close()
    }
}