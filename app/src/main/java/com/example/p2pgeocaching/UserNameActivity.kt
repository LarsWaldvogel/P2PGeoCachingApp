package com.example.p2pgeocaching

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.p2pgeocaching.databinding.ActivityUserNameBinding


class UserNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //
        val binding = ActivityUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
