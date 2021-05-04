package com.example.p2pgeocaching

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File

// TODO add manifest to get bluetooth permissions
// TODO add user interface
// TODO add bluetooth transfer function
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val file = File(context.filesDir, filename)
        setContentView(R.layout.activity_main)
    }
}





// TODO: user interface needs the following screens:
//  - when first opened: "what is your name?" screen which saves username
//  - list of all caches with button to "create cache" and "connect to others" (main menu)
//  (when empty show "connect or create"-message)
//  - forms to create cache (with input validation)
//  - page that shows the private key of owned cache
//  - cache detail view with hallOfFame and "found it" button
//  - form to enter found private key
//  - screen for bluetooth transfer
