package com.example.p2pgeocaching.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * This Class has all functions to connect to devices via Bluetooth
 */

class BluetoothHandler(val activity: BluetoothTransferActivity) {

    companion object {
        const val TAG = "BluetoothHandler"
    }

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val appName: String = "P2P_Geocaching"
    private val uuid: UUID = UUID.fromString("708305f5-933b-40ad-b687-5b8ebfd8b5c6")

    var serverAcceptThread: AcceptThread? = null  // Server
    var clientConnectThread: ConnectThread? = null    // Client

    val state: BluetoothBroadcast = BluetoothBroadcast(this, activity)

    val devices: ArrayList<BluetoothDevice?> = ArrayList() // If you want to display the devices found

    /**
     * This method makes your device discoverable for others for 100 sec
     */
    fun discoverable(enabled: Boolean) {
        if (enabled) {
            Log.d(TAG, "Making device discoverable")

            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 100)
            activity.startActivity(discoverableIntent)
        } else {
            Log.d(TAG, "Making device not discoverable anymore")

            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1)
            activity.startActivity(discoverableIntent)
        }


    }

    /**
     * This method searches for discoverable devices in the surrounding
     */
    fun startDiscovery() {
        Log.d(TAG, "Looking for unpaired devices.")

        bluetoothAdapter?.startDiscovery()
    }

    /**
     * This method starts the serverThread and listens for incoming connections
     */
    fun startServer() {
        Log.d(TAG, "Starting server thread and waiting for incoming connections ...")

        serverAcceptThread = AcceptThread()
        serverAcceptThread?.start()
    }

    /**
     * This method starts a clientThread and tries to connect with a server device
     */
    fun connectToServer(device: BluetoothDevice?) {
        Log.d(TAG, "Connecting Bluetooth ...")

        clientConnectThread = ConnectThread(device)
        clientConnectThread?.start()
    }

    /**
     * This method stops the connection by closing their sockets
     */
    fun stop() {
        clientConnectThread?.cancel()
        serverAcceptThread?.cancel()

        Log.d(TAG, "Bluetooth Handler closed all sockets")
    }

    /**
     * This inner class represents the server thread who accepts incoming connections
     */
    inner class AcceptThread: Thread() {

        private val serverSocket: BluetoothServerSocket? = bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(appName, uuid)
        var socket: BluetoothSocket? = null
        private val buffer: ByteArray = ByteArray(1024)

        override fun run() {
            var inLoop = true

            while(inLoop) {
                try {
                    socket = serverSocket?.accept()
                    socket?.remoteDevice?.name?.let { Log.d(TAG, "The name of the remote device: $it") }
                    while(true) {
                        val json = "json file" // TODO get json ownfeed file
                        write(json.toByteArray())
                        try {
                            val input = BufferedReader(InputStreamReader(socket!!.inputStream)) //socket!!.inputStream.read(buffer)
                            val inputText = input.readText() // TODO convert back to json file

                        } catch (e: IOException) {
                            Log.e(TAG, "AcceptThread: inputstream error")
                        }
                        serverSocket?.close()
                        inLoop = false
                        break
                    }

                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    inLoop = false
                }
            }
        }

        private fun write(bytes: ByteArray) {
            try {
                socket?.outputStream?.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "outputstream error", e)

            }
        }


        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    /**
     * This inner class represents the client thread who tries to connect to the server
     */
    inner class ConnectThread(val device: BluetoothDevice?): Thread() {

        private val clientSocket: BluetoothSocket? = device?.createRfcommSocketToServiceRecord(uuid)
        private val buffer: ByteArray = ByteArray(1024)

        override fun run() {
            bluetoothAdapter?.cancelDiscovery()
            clientSocket?.connect()
            Log.d(TAG, "run: ConnectThread connected.")
            write("json".toByteArray()) // TODO get jsonfile

            try {
                clientSocket?.inputStream?.read(buffer)
            } catch (e: IOException) {
                Log.e(TAG, "ConnectThread: inputstream error")
            }
            clientSocket?.close()
        }

        private fun write(bytes: ByteArray) {
            try {
                clientSocket?.outputStream?.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "outputstream error", e)
            }
        }

        fun cancel() {
            try {
                clientSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }




}