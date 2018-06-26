package com.geekyind.maritime.fishfeeder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask.execute
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.log.DJILog
import dji.sdk.base.BaseProduct
import dji.sdk.products.Aircraft
import dji.sdk.products.HandHeld
import dji.sdk.sdkmanager.DJISDKManager
import java.util.concurrent.atomic.AtomicBoolean


class DjiDeviceConnectionActivity : AppCompatActivity(), View.OnClickListener {

    private val tag = DjiDeviceConnectionActivity::class.java.name

    private val requiredPermissionsArray = arrayOf(Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)

    private val missingPermission = ArrayList<String>()
    private val isRegistrationInProgress = AtomicBoolean(false)
    private val requestPermissionCode = 12345
//    private val flagConnectionChanged = "dji_sdk_connection_change"


    private var mTextConnectionStatus: TextView? = null
    private var mTextProduct: TextView? = null
    private var mVersionTv: TextView? = null
    private var mBtnOpen: Button? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        setContentView(R.layout.activity_dji_device_connection)
        initUI()

        // Register the broadcast receiver for receiving the device connection's changes.
        val filter = IntentFilter()
        filter.addAction(DjiFishFeederApp.FLAG_CONNECTION_CHANGE)
        registerReceiver(mReceiver, filter)
    }

    public override fun onResume() {
        Log.e(tag, "onResume")
        super.onResume()
    }

    public override fun onPause() {
        Log.e(tag, "onPause")
        super.onPause()
    }

    public override fun onStop() {
        Log.e(tag, "onStop")
        super.onStop()
    }

//    fun onReturn(view: View) {
//        Log.e(tag, "onReturn")
//        this.finish()
//    }

    override fun onDestroy() {
        Log.e(tag, "onDestroy")
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private fun initUI() {
        mTextConnectionStatus = findViewById(R.id.text_connection_status)
        mTextProduct = findViewById(R.id.text_product_info)
        mVersionTv = findViewById(R.id.textView2)
        mVersionTv!!.text = resources.getString(R.string.sdk_version, DJISDKManager.getInstance().sdkVersion)
        mBtnOpen = findViewById<View>(R.id.btn_open) as Button
        mBtnOpen!!.setOnClickListener(this)
        mBtnOpen!!.isEnabled = false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_open -> {
            }
            else -> {
            }
        }
    }




    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private fun checkAndRequestPermissions() {
        // Check for permissions
        for (eachPermission in requiredPermissionsArray) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission)
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            startSDKRegistration()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showToast("Need to grant the permissions!")
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(arrayOfNulls(missingPermission.size)),
                    requestPermissionCode)
        }
    }

    /**
     * Result of runtime permission request
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check for granted permission and remove from missing list
        if (requestCode == requestPermissionCode) {
            for (i in grantResults.indices.reversed()) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i])
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration()
        } else {
            showToast("Missing permissions!!!")
        }
    }


    private fun startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            execute {
                showToast("registering, pls wait...")
                DJISDKManager.getInstance().registerApp(applicationContext, object : DJISDKManager.SDKManagerCallback {
                    override fun onRegister(djiError: DJIError) {
                        if (djiError === DJISDKError.REGISTRATION_SUCCESS) {
                            DJILog.e("App registration", DJISDKError.REGISTRATION_SUCCESS.description)
                            DJISDKManager.getInstance().startConnectionToProduct()
                            showToast("Register Success")
                        } else {
                            showToast("Register sdk fails, check network is available")
                        }
                        Log.v(tag, djiError.description)
                    }

                    override fun onProductChange(oldProduct: BaseProduct, newProduct: BaseProduct) {
                        Log.d(tag, String.format("onProductChanged oldProduct:%s, newProduct:%s", oldProduct, newProduct))
                    }
                })
            }
        }
    }


    private fun showToast(toastMsg: String) {
        runOnUiThread { Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_LONG).show() }
    }

    private fun refreshSDKRelativeUI() {

        val mProduct = DjiFishFeederApp.getProductInstance()

        if (null != mProduct && mProduct.isConnected) {
            Log.v(tag, "refreshSDK: True")
            mBtnOpen!!.isEnabled = true

            var str = "Status Unknown"

            if(mProduct is Aircraft){
                str = "Status: Aircraft Connected"
            } else if (mProduct is HandHeld) {
                str = "Status: Handheld Connected"
            }

            mTextConnectionStatus!!.text = str

            if (null != mProduct.model) {
                mTextProduct!!.text = mProduct.model.displayName
            } else {
                mTextProduct!!.setText(R.string.product_information)
            }
        } else {
            Log.v(tag, "refreshSDK: False")
            mBtnOpen!!.isEnabled = false
            mTextProduct!!.setText(R.string.connection_loose)
            mTextConnectionStatus!!.setText(R.string.connection_loose)
        }
    }


    private var mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refreshSDKRelativeUI()
            Log.v(tag,"Broadcast received event.")
        }
    }
}