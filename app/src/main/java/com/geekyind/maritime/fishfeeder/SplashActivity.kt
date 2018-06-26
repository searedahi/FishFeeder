package com.geekyind.maritime.fishfeeder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent



class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, DjiDeviceConnectionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
