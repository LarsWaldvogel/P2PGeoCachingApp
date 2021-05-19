package com.example.p2pgeocaching.bluetooth

import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.os.Bundle
//import android.support.v4.app.DialogFragment
import androidx.fragment.app.DialogFragment



class ChooseClient: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(this.activity)
        builder.setTitle("Send cache to")
        // To show list of connected devices, create ListAdapter/ArrayAdapter(subclass of ListAdapter)
        // and a listener
        builder.setAdapter(BluetoothTransfer().arrayOfDevices) { _, chosenClient: Int ->
            val cache = " "
            BluetoothClient(BluetoothTransfer().listOfDevices!!.elementAt(chosenClient), cache).start() // elementAtOrNull(which)
        }
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }
}