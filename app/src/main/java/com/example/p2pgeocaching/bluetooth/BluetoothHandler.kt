package com.example.p2pgeocaching.bluetooth

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.p2pgeocaching.data.FeedData
import java.io.*
import java.util.*

/**
 * This Class has all functions to connect to devices via Bluetooth
 */

class BluetoothHandler(val activity: BluetoothTransferActivity, val manager: BluetoothManager) {

    companion object {
        const val TAG = "BluetoothHandler"
    }

    var context: File = File("file")
    val bluetoothAdapter: BluetoothAdapter = manager.adapter

    private val appName: String = "P2P_Geocaching"
    private val uuid: UUID = UUID.fromString("708305f5-933b-40ad-b687-5b8ebfd8b5c6")

    var serverAcceptThread: AcceptThread? = null  // Server
    var clientConnectThread: ConnectThread? = null    // Client

    val state: BluetoothBroadcast = BluetoothBroadcast(this, activity)

    var devices: ArrayList<BluetoothDevice?> = ArrayList() // If you want to display the devices found
    lateinit var bluetoothDeviceListAdapter: BluetoothDeviceListAdapter

    /**
     * This method makes your device discoverable for others for 100 sec
     */
    fun discoverable(enabled: Boolean) {
        if (enabled) {
            Log.i(TAG, "Making device discoverable")

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
        Log.i(TAG, "Looking for unpaired devices.")
        if(bluetoothAdapter == null) {
            Log.i(TAG, "NULL")
        }
        Log.i(TAG, "BA Adress = "+bluetoothAdapter.address)
        Log.i(TAG, "BA Name = "+bluetoothAdapter.name)
        bluetoothAdapter?.startDiscovery()
    }

    /**
     * This method starts the serverThread and listens for incoming connections
     */
    fun startServer(c:  File) {
        Log.i(TAG, "Starting server thread and waiting for incoming connections ...")
        context = c
        serverAcceptThread = AcceptThread()
        serverAcceptThread?.start()
    }

    /**
     * This method starts a clientThread and tries to connect with a server device
     */
    fun connectToServer(device: BluetoothDevice?, c: File) {
        Log.i(TAG, "Connecting Bluetooth ...")
        context = c
        clientConnectThread = ConnectThread(device)
        clientConnectThread?.start()
    }

    /**
     * This method stops the connection by closing their sockets
     */
    fun stop() {
        clientConnectThread?.cancel()
        serverAcceptThread?.cancel()

        Log.i(TAG, "Bluetooth Handler closed all sockets")
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
                    Log.i(TAG, "Server is waiting in try block before accept")
                    if (serverSocket == null) {
                        Log.i(TAG, "Serversocket is null")
                    }
                    socket = serverSocket?.accept()
                    Log.i(TAG, "socket accepted")
                    //socket?.remoteDevice?.name?.let { Log.d(TAG, "The name of the remote device: $it") }
                    while(true) {
                        val fd = FeedData(context)
                        if (fd.stringFileContent.length != 0) {
                            Log.i(TAG, "feedToSend = " + fd.stringFileContent)
                            val bytes = ByteArray(fd.stringFileContent.length)
                            //val serializedFeed = FeedData.serializer(fd)
                            Log.i(TAG, "File to bytes, bytearraysize: " + fd.stringFileContent.length)
                            val charset = Charsets.UTF_8
                            write(fd.stringFileContent.toByteArray(charset))
                            Log.i(TAG, "write bytes = "+fd.stringFileContent.toByteArray())
                            Log.i(TAG, "write bytes = "+fd.stringFileContent.toByteArray().contentToString()
                            )
                            // TODO* Define Protocol!
                            /*try {
                                var inputStream =
                                val input =
                                    BufferedReader(InputStreamReader(socket!!.inputStream)) //socket!!.inputStream.read(buffer)
                                val inputText = input.readText()
                                if (inputText.equals("OK")) {
                                    Log.e(TAG, "Everything went great")
                                    break
                                }
                            } catch (e: IOException) {
                                Log.e(TAG, "AcceptThread: inputstream error")
                            }*/
                        }
                            serverSocket?.close()
                            Log.i(TAG, "close server")
                            inLoop = false
                            break
                    }

                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed")
                    inLoop = false
                    break
                }
                break
            }
        }

        private fun write(bytes: ByteArray) {
            Log.i(TAG, "in write")
            try {
                socket?.outputStream?.write(bytes)
                Log.i(TAG, "write-socket write")
            } catch (e: IOException) {
                Log.e(TAG, "outputstream error", e)

            }
        }

        private fun read(bytes: ByteArray) {
            try {
                val input = BufferedReader(InputStreamReader(socket!!.inputStream)) //socket!!.inputStream.read(buffer)
                val inputText = input.readText()
                if(inputText.equals("OK")) {

                }
            } catch (e: IOException) {
                Log.e(TAG, "AcceptThread: inputstream error")
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
        private val buffer: ByteArray = ByteArray(1024) // How to find Filesize

        override fun run() {
            Log.i(TAG, "ConnectThread: in run()")
            bluetoothAdapter?.cancelDiscovery()
            Log.i(TAG, "ConnectThread: after cancelDiscovery()/before connect")
            clientSocket?.connect()
            Log.i(TAG, "run: ConnectThread connected.")
            var receivedFeedFile = File(context, "rcvFile")

            try {
                Log.i(TAG, "In try")
                read(buffer, receivedFeedFile)
                Log.i(TAG, "read File")
                //write("OK".toByteArray())
                Log.i(TAG, "OK-Statement")
            } catch (e: IOException) {
                Log.e(TAG, "ConnectThread: inputstream error")
            }
            clientSocket?.close()
        }

        private fun read(bytes: ByteArray, file: File) {
            Log.i(TAG, "Started read")
            clientSocket?.inputStream?.read(bytes)
            val charset = Charsets.UTF_8
            Log.i(TAG, "Inputstream "+bytes.toString(charset))
            /*Log.i(TAG, "Inputstream "+bytes.contentToString())
            var fos: FileOutputStream? = null
            try {
                Log.i(TAG, "read-Try")
                fos = FileOutputStream(file)
                Log.i(TAG, "FileOutputStream")
                fos.write(bytes)
                Log.i(TAG, "FileOutputStream write")
            } finally {
                fos?.close()
                Log.i(TAG, "close")
            }*/
            if (!file.exists()) {
                Log.i(TAG, "File doesn't exist")
                file.createNewFile()
                Log.i(TAG, "CreatedFile")
            } else {
                file.delete()
                file.createNewFile()
            }
            Log.i(TAG, "Going to write in File")
            file.writeText(bytes.toString(charset))
            Log.i(TAG, "Wrote to File = "+file.readText())
            val fd = FeedData(file, context)
            Log.i(TAG, "dataToFeed over")
        }

        private fun write(bytes: ByteArray) {
            Log.i(TAG, "write()")
            try {
                clientSocket?.outputStream?.write(bytes)
                Log.i(TAG, "outputstream of clientsocket")
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