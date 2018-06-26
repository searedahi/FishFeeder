package com.geekyind.maritime.fishfeeder

import android.app.Application
import android.content.Context
import com.secneo.sdk.Helper

class DjiHardwareConnector: Application() {

    private var djiFishFeederApp : DjiFishFeederApp? = null

    override fun attachBaseContext(paramContext: Context) {
        super.attachBaseContext(paramContext)
        Helper.install(this@DjiHardwareConnector)
        if (djiFishFeederApp == null) {
            djiFishFeederApp = DjiFishFeederApp()
            djiFishFeederApp!!.setContext(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        djiFishFeederApp!!.onCreate()
    }
}